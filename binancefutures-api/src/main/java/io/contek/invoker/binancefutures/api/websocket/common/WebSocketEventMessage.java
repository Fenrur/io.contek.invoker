package io.contek.invoker.binancefutures.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;

public abstract class WebSocketEventMessage extends AnyWebSocketMessage {

  public String e;
  public Long E; // Event time
}
