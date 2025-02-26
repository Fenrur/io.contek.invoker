package io.contek.invoker.hbdminverse.api.websocket.common.notification;

import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.commons.websocket.*;
import io.contek.invoker.security.ICredential;

import java.net.URI;
import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.net.UrlEscapers.urlFormParameterEscaper;
import static io.contek.invoker.hbdminverse.api.websocket.user.constants.AuthTypeKeys._api;
import static io.contek.invoker.hbdminverse.api.websocket.user.constants.OpKeys._auth;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import static java.time.temporal.ChronoField.MILLI_OF_SECOND;

final class NotificationWebSocketAuthenticator implements IWebSocketAuthenticator {

  private static final DateTimeFormatter FORMATTER = ISO_LOCAL_DATE_TIME.withZone(UTC);

  private final ICredential credential;
  private final String path;
  private final NotificationWebSocketRequestIdGenerator requestIdGenerator;
  private final WebSocketContext context;
  private final Clock clock;

  private final AtomicBoolean authenticated = new AtomicBoolean();
  private NotificationWebSocketAuthRequest pendingCommandHolder = null;

  NotificationWebSocketAuthenticator(
      ICredential credential,
      String path,
      NotificationWebSocketRequestIdGenerator requestIdGenerator,
      WebSocketContext context,
      Clock clock) {
    this.credential = credential;
    this.path = path;
    this.requestIdGenerator = requestIdGenerator;
    this.context = context;
    this.clock = clock;
  }

  @Override
  public void handshake(WebSocketSession session) {
    if (credential.isAnonymous()) {
      return;
    }

      NotificationWebSocketAuthRequest request = new NotificationWebSocketAuthRequest();
      request.op = _auth;
      request.type = _api;
      request.cid = requestIdGenerator.generateNext();
      request.AccessKeyId = credential.getApiKeyId();
      request.SignatureMethod = "HmacSHA256";
      request.SignatureVersion = "2";
      request.Timestamp = FORMATTER.format(clock.instant().with(MILLI_OF_SECOND, 0));
      request.Signature = generateSignature(request);
      session.send(request);
      pendingCommandHolder = request;
  }

  @Override
  public boolean isPending() {
    return pendingCommandHolder != null;
  }

  @Override
  public boolean isCompleted() {
    return authenticated.get() || credential.isAnonymous();
  }

  @Override
  public void onMessage(AnyWebSocketMessage message, WebSocketSession session) {
    if (isCompleted()) {
      return;
    }

    if (!(message instanceof NotificationWebSocketConfirmation)) {
      return;
    }
    NotificationWebSocketConfirmation response = (NotificationWebSocketConfirmation) message;

      NotificationWebSocketAuthRequest request = pendingCommandHolder;
      if (request == null) {
        return;
      }

      if (!request.cid.equals(response.cid)) {
        return;
      }

      pendingCommandHolder = null;
      if (response.err_code != 0) {
        throw new WebSocketIllegalMessageException(response.err_code + ": " + response.err_msg);
      }

      reset();
      authenticated.set(true);
  }

  @Override
  public void afterDisconnect() {
    reset();
  }

  private String generateSignature(NotificationWebSocketAuthRequest request) {
    RestParams.Builder builder = RestParams.newBuilder();
    builder.add("AccessKeyId", request.AccessKeyId);
    builder.add("SignatureMethod", request.SignatureMethod);
    builder.add("SignatureVersion", request.SignatureVersion);
    builder.add("Timestamp", request.Timestamp);
    RestParams withIdentity = builder.build(true);
    String queryString = withIdentity.getQueryString(urlFormParameterEscaper());
    String payload =
        String.join("\n", "GET", URI.create(context.baseUrl()).getHost(), path, queryString);
    return credential.sign(payload);
  }

  private void reset() {
    authenticated.set(false);
      pendingCommandHolder = null;
  }
}
