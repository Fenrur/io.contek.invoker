package io.contek.invoker.binancefutures.api.websocket.market;

import io.contek.invoker.commons.api.websocket.AnyWebSocketMessage;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class AggTradeEvent extends AnyWebSocketMessage {

  public String e;
  public Long E; // Event time
  public String s; // Symbol
  public Long a; // Aggregate trade ID
  public Double p; // Price
  public Double q; // Quantity
  public Long f; // First trade ID
  public Long l; // Last trade ID
  public Long T; // Trade time
  public Boolean m; // Is the buyer the market maker?
}
