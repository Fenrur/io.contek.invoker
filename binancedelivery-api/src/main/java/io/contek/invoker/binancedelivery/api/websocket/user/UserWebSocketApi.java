package io.contek.invoker.binancedelivery.api.websocket.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.binancedelivery.api.rest.user.UserRestApi;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.websocket.*;
import io.contek.invoker.security.ICredential;
import io.vertx.core.Future;

import static io.contek.invoker.binancedelivery.api.ApiFactory.RateLimits.ONE_WEB_SOCKET_CONNECTION;

public final class UserWebSocketApi extends BaseWebSocketApi {

  private final WebSocketContext context;
  public AccountUpdateChannel accountUpdateChannel;
  public OrderUpdateChannel orderUpdateChannel;
  public MarginCallChannel marginCallChannel;
  public AccountConfigUpdateChannel accountConfigUpdateChannel;

  public UserWebSocketApi(IActor actor, WebSocketContext context, UserRestApi userRestApi) {
    super(
        actor,
        UserWebSocketParser.getInstance(),
        IWebSocketAuthenticator.noOp(),
        new UserWebSocketLiveKeeper(userRestApi, actor.clock()));
    this.context = context;
  }

  public AccountUpdateChannel getAccountUpdateChannel() {
    if (accountUpdateChannel == null) {
      accountUpdateChannel = new AccountUpdateChannel();
      attach(accountUpdateChannel);
    }
    return accountUpdateChannel;
  }

  public OrderUpdateChannel getOrderUpdateChannel() {
    if (orderUpdateChannel == null) {
      orderUpdateChannel = new OrderUpdateChannel();
      attach(orderUpdateChannel);
    }
    return orderUpdateChannel;
  }

  public MarginCallChannel getMarginCallChannel() {
    if (marginCallChannel == null) {
      marginCallChannel = new MarginCallChannel();
      attach(marginCallChannel);
    }
    return marginCallChannel;
  }

  public AccountConfigUpdateChannel getLeverageUpdateChannel() {
    if (accountConfigUpdateChannel == null) {
      accountConfigUpdateChannel = new AccountConfigUpdateChannel();
      attach(accountConfigUpdateChannel);
    }
    return accountConfigUpdateChannel;
  }

  @Override
  protected WebSocketCall createCall(ICredential credential) {
    UserWebSocketLiveKeeper liveKeeper = (UserWebSocketLiveKeeper) getLiveKeeper();
    final Future<String> init = liveKeeper.init();
    return httpClient ->
        init.map(listenKey -> WebSocketCall.fromUrl(context.baseUrl() + "/ws/" + listenKey))
            .flatMap(webSocketCall -> webSocketCall.submit(httpClient));
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_WEB_SOCKET_CONNECTION;
  }

  @Override
  protected void checkErrorMessage(AnyWebSocketMessage message) throws WebSocketRuntimeException {
    if (message instanceof UserDataStreamExpiredEvent) {
      throw new WebSocketSessionExpiredException();
    }
  }
}
