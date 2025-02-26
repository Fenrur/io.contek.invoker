package io.contek.invoker.bitmex.api.websocket.common;

import io.contek.invoker.commons.websocket.AnyWebSocketMessage;
import io.contek.invoker.commons.websocket.IWebSocketRawTextMessage;

import static io.contek.invoker.commons.websocket.constants.WebSocketPingPongKeys._pong;

public final class WebSocketPong extends AnyWebSocketMessage implements IWebSocketRawTextMessage {

  @Override
  public String getRawText() {
    return _pong;
  }
}
