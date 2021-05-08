package io.contek.invoker.commons.websocket;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.IRateLimitThrottle;
import io.contek.invoker.commons.actor.ratelimit.RateLimitQuota;
import io.contek.invoker.security.ICredential;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.annotation.concurrent.ThreadSafe;
import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static org.slf4j.LoggerFactory.getLogger;

@ThreadSafe
public abstract class BaseWebSocketApi implements IWebSocketApi {

  private static final Logger log = getLogger(BaseWebSocketApi.class);

  private final IActor actor;
  private final IWebSocketMessageParser parser;
  private final IWebSocketAuthenticator authenticator;
  private final IWebSocketLiveKeeper liveKeeper;

  private final Handler handler = new Handler();
  private final ScheduledExecutorService scheduler = newSingleThreadScheduledExecutor();

  private final AtomicReference<WebSocketSession> sessionHolder = new AtomicReference<>();
  private final AtomicReference<ScheduledFuture<?>> scheduleHolder = new AtomicReference<>();
  private final WebSocketComponentManager components = new WebSocketComponentManager();

  protected BaseWebSocketApi(
      IActor actor,
      IWebSocketMessageParser parser,
      IWebSocketAuthenticator authenticator,
      IWebSocketLiveKeeper liveKeeper) {
    this.actor = actor;
    this.parser = parser;
    this.authenticator = authenticator;
    this.liveKeeper = liveKeeper;
  }

  public final void attach(IWebSocketComponent channel) {
    synchronized (components) {
      components.attach(channel);
      activate();
    }
  }

  protected final IWebSocketMessageParser getParser() {
    return parser;
  }

  protected final IWebSocketAuthenticator getAuthenticator() {
    return authenticator;
  }

  protected final IWebSocketLiveKeeper getLiveKeeper() {
    return liveKeeper;
  }

  protected abstract ImmutableList<RateLimitQuota> getRequiredQuotas();

  protected abstract WebSocketCall createCall(ICredential credential);

  protected abstract void checkErrorMessage(AnyWebSocketMessage message)
      throws WebSocketRuntimeException;

  private void forwardMessage(String text) {
    AnyWebSocketMessage message = parser.parse(text);
    forwardMessage(message);
  }

  private void forwardMessage(ByteString bytes) {
    AnyWebSocketMessage message = parser.parse(bytes.toByteArray());
    forwardMessage(message);
  }

  private void forwardMessage(AnyWebSocketMessage message) {
    checkErrorMessage(message);

    synchronized (sessionHolder) {
      WebSocketSession session = sessionHolder.get();
      synchronized (authenticator) {
        authenticator.onMessage(message, session);
      }
      synchronized (liveKeeper) {
        liveKeeper.onMessage(message, session);
      }
      synchronized (components) {
        components.onMessage(message, session);
      }
    }
  }

  private void connect() {
    synchronized (sessionHolder) {
      sessionHolder.updateAndGet(
          oldValue -> {
            if (oldValue != null) {
              return oldValue;
            }
            WebSocketCall call = createCall(actor.getCredential());
            IRateLimitThrottle throttle = actor.getRateLimitThrottle();
            throttle.acquire(getClass().getSimpleName(), getRequiredQuotas());

            WebSocketSession session = call.submit(actor.getHttpClient(), handler);
            activate();
            return session;
          });
    }
  }

  private void afterDisconnect() {
    synchronized (sessionHolder) {
      sessionHolder.set(null);
      synchronized (authenticator) {
        authenticator.afterDisconnect();
      }
      synchronized (components) {
        components.afterDisconnect();
      }
    }
  }

  private void heartbeat() {
    try {
      synchronized (sessionHolder) {
        synchronized (components) {
          components.refresh();
          WebSocketSession session = sessionHolder.get();
          if (session == null) {
            if (!components.hasComponent()) {
              deactivate();
              return;
            }
            if (components.hasActiveComponent()) {
              connect();
            }
            return;
          }

          if (!components.hasActiveComponent()) {
            log.info("No active components. Closing session.");
            session.close();
            return;
          }

          if (!authenticator.isCompleted()) {
            authenticator.handshake(session);
            return;
          }

          components.heartbeat(session);
          liveKeeper.onHeartbeat(session);
        }
      }
    } catch (Throwable t) {
      log.error("Heartbeat failed.", t);
    }
  }

  private void activate() {
    synchronized (scheduleHolder) {
      scheduleHolder.updateAndGet(
          oldValue -> {
            if (oldValue != null && !oldValue.isDone()) {
              return oldValue;
            }
            return scheduler.scheduleWithFixedDelay(this::heartbeat, 0, 1, TimeUnit.SECONDS);
          });
    }
  }

  private void deactivate() {
    synchronized (scheduleHolder) {
      scheduleHolder.updateAndGet(
          oldValue -> {
            if (oldValue == null) {
              return null;
            }
            if (!oldValue.isDone()) {
              oldValue.cancel(true);
            }
            return null;
          });
    }
  }

  @ThreadSafe
  private final class Handler extends WebSocketListener {

    @Override
    public void onClosed(WebSocket ws, int code, String reason) {
      log.info("Session is closed: {} {}.", code, reason);
      try {
        afterDisconnect();
      } catch (Throwable t) {
        log.error("Failed to handle closed session.", t);
      }
    }

    @Override
    public void onFailure(WebSocket ws, Throwable t, @Nullable Response response) {
      if (t instanceof SocketTimeoutException) {
        log.warn("Shutting down inactive session.", t);
      } else if (t instanceof EOFException) {
        log.warn("Server closed connection.", t);
      } else if (t instanceof IOException) {
        log.warn("Connection interrupted.", t);
      } else if (t instanceof WebSocketRuntimeException) {
        log.warn("Encountered runtime exception.", t);
      } else {
        log.error("Encountered unknown error: {}.", response, t);
      }

      try {
        ws.cancel();
        afterDisconnect();
      } catch (Throwable t2) {
        log.error("Failed to handle failure.", t2);
      }
    }

    @Override
    public void onMessage(WebSocket ws, String text) {
      forwardMessage(text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
      forwardMessage(bytes);
    }

    @Override
    public void onOpen(WebSocket ws, Response response) {
      log.info("Session is open: {}.", response);
    }
  }
}
