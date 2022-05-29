package io.contek.invoker.binancespot.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public abstract class WebSocketStreamMessage<T extends WebSocketEventMessage>
    extends AnyWebSocketMessage {

  public String stream;
  public T data;
}
