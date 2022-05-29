package io.contek.invoker.commons.websocket;

import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.contek.invoker.commons.websocket.ConsumerState.*;
import static io.contek.invoker.commons.websocket.SubscriptionState.*;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class BaseWebSocketChannel<
        Id extends BaseWebSocketChannelId<Message>, Message extends AnyWebSocketMessage>
    implements IWebSocketComponent {

  private static final Logger log = getLogger(BaseWebSocketChannel.class);

  private final Id id;

  private final AtomicReference<SubscriptionState> stateHolder =
      new AtomicReference<>(UNSUBSCRIBED);
  private final List<ISubscribingConsumer<Message>> consumers = new LinkedList<>();

  protected BaseWebSocketChannel(Id id) {
    this.id = id;
  }

  public final Id getId() {
    return id;
  }

  public final void addConsumer(ISubscribingConsumer<Message> consumer) {
    synchronized (consumers) {
      synchronized (stateHolder) {
        SubscriptionState state = stateHolder.get();
        consumer.onStateChange(state);
      }
      consumers.add(consumer);
    }
  }

  @Override
  public final void heartbeat(WebSocketSession session) {
    synchronized (consumers) {
      ConsumerState childConsumerState = getChildConsumerState();

      synchronized (stateHolder) {
        SubscriptionState currentState = stateHolder.get();
        SubscriptionState newState = null;
        if (currentState == SUBSCRIBED && childConsumerState == IDLE) {
          log.info("Unsubscribing channel {}.", id);
          newState = unsubscribe(session);
          if (newState == SUBSCRIBED || newState == SUBSCRIBING) {
            log.error("Channel {} has invalid state after unsubscribe: {}.", id, newState);
          }
        } else if (currentState == UNSUBSCRIBED && childConsumerState == ACTIVE) {
          log.info("Subscribing channel {}.", id);
          newState = subscribe(session);
          if (newState == UNSUBSCRIBED || newState == UNSUBSCRIBING) {
            log.error("Channel {} has invalid state after subscribe: {}.", id, newState);
          }
        }

        if (newState != null) {
          setState(newState);
        }
      }
    }
  }

  @Override
  public final ConsumerState getState() {
    synchronized (consumers) {
      if (getChildConsumerState() == ACTIVE) {
        return ACTIVE;
      }
    }

    synchronized (stateHolder) {
      return stateHolder.get() != UNSUBSCRIBED ? ACTIVE : IDLE;
    }
  }

  private void setState(SubscriptionState state) {
    synchronized (consumers) {
      synchronized (stateHolder) {
        consumers.forEach(consumer -> consumer.onStateChange(state));
        stateHolder.set(state);
      }
    }
  }

  @Override
  public final void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
    synchronized (consumers) {
      Message casted = tryCast(message);
      if (casted != null) {
        consumers.forEach(consumer -> consumer.onNext(casted));
      }
    }

    SubscriptionState newState = getState(message);
    if (newState != null) {
      log.info("Channel {} is now {}.", id, newState);
      setState(newState);
    }
  }

  @Override
  public final void afterDisconnect() {
    reset();
    setState(UNSUBSCRIBED);
  }

  public abstract Class<Message> getMessageType();

  protected abstract SubscriptionState subscribe(WebSocketSession session);

  protected abstract SubscriptionState unsubscribe(WebSocketSession session);

  protected abstract SubscriptionState getState(AnyWebSocketMessage message);

  protected abstract void reset();

  private ConsumerState getChildConsumerState() {
    synchronized (consumers) {
      consumers.removeIf(consumer -> consumer.getState() == TERMINATED);
      return consumers.stream().anyMatch(consumer -> consumer.getState() == ACTIVE) ? ACTIVE : IDLE;
    }
  }

  private Message tryCast(AnyWebSocketMessage message) {
    if (!getMessageType().isAssignableFrom(message.getClass())) {
      return null;
    }
    Message casted = getMessageType().cast(message);
    return id.accepts(casted) ? casted : null;
  }
}
