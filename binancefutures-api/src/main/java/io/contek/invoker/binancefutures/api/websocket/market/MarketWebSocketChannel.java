package io.contek.invoker.binancefutures.api.websocket.market;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.binancefutures.api.websocket.WebSocketRequestIdGenerator;
import io.contek.invoker.binancefutures.api.websocket.common.WebSocketCommand;
import io.contek.invoker.binancefutures.api.websocket.common.WebSocketCommandConfirmation;
import io.contek.invoker.binancefutures.api.websocket.common.WebSocketStreamMessage;
import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.BaseWebSocketChannel;
import io.contek.invoker.commons.websocket.SubscriptionState;
import io.contek.invoker.commons.websocket.WebSocketSession;

import java.util.concurrent.atomic.AtomicReference;

import static io.contek.invoker.binancefutures.api.websocket.common.constants.WebSocketMethods.SUBSCRIBE;
import static io.contek.invoker.binancefutures.api.websocket.common.constants.WebSocketMethods.UNSUBSCRIBE;
import static io.contek.invoker.commons.websocket.SubscriptionState.*;

public abstract class MarketWebSocketChannel<
        Id extends MarketWebSocketChannelId<Message>, Message extends WebSocketStreamMessage<?>>
    extends BaseWebSocketChannel<Id, Message> {

  private final WebSocketRequestIdGenerator requestIdGenerator;

  private WebSocketCommand pendingCommandHolder = null;

  protected MarketWebSocketChannel(Id id, WebSocketRequestIdGenerator requestIdGenerator) {
    super(id);
    this.requestIdGenerator = requestIdGenerator;
  }

  @Override
  protected final SubscriptionState subscribe(WebSocketSession session) {
      if (pendingCommandHolder != null) {
        throw new IllegalStateException();
      }

      Id id = getId();
      WebSocketCommand command = new WebSocketCommand();
      command.method = SUBSCRIBE;
      command.params = ImmutableList.of(id.getStreamName());
      command.id = requestIdGenerator.getNextRequestId();
      session.send(command);
      pendingCommandHolder = command;
    return SUBSCRIBING;
  }

  @Override
  protected final SubscriptionState unsubscribe(WebSocketSession session) {
      if (pendingCommandHolder != null) {
        throw new IllegalStateException();
      }

      Id id = getId();
      WebSocketCommand command = new WebSocketCommand();
      command.method = UNSUBSCRIBE;
      command.params = ImmutableList.of(id.getStreamName());
      command.id = requestIdGenerator.getNextRequestId();
      session.send(command);
      pendingCommandHolder = command;
    return UNSUBSCRIBING;
  }

  @Override
  protected final SubscriptionState getState(AnyWebSocketMessage message) {
    if (!(message instanceof WebSocketCommandConfirmation)) {
      return null;
    }
    WebSocketCommandConfirmation confirmation = (WebSocketCommandConfirmation) message;

      WebSocketCommand command = pendingCommandHolder;
      if (command == null) {
        return null;
      }
      if (confirmation.id == null || !confirmation.id.equals(command.id)) {
        return null;
      }
      reset();
    return switch (command.method) {
      case SUBSCRIBE -> SUBSCRIBED;
      case UNSUBSCRIBE -> UNSUBSCRIBED;
      default -> throw new IllegalStateException();
    };
  }

  @Override
  protected final void reset() {
      pendingCommandHolder = null;
  }
}
