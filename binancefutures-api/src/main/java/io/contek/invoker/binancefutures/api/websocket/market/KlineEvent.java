package io.contek.invoker.binancefutures.api.websocket.market;

import io.contek.invoker.binancefutures.api.websocket.common.WebSocketEventMessage;

public class KlineEvent extends WebSocketEventMessage {

  public String s; // Symbol
  public Candle k;

  public static class Candle {

    public Long t; // Kline start time
    public Long T; // Kline close time
    public String s; // Symbol
    public String i; // Interval
    public Long f; // First trade ID
    public Long L; // Last trade ID
    public Double o; // Open price
    public Double c; // Close price
    public Double h; // High price
    public Double l; // Low price
    public Double v; // Base asset volume
    public Integer n; // Number of trades
    public Boolean x; // Is this kline closed?
    public Double q; // Quote asset volume
    public Double V; // Taker buy base asset volume
    public Double Q; // Taker buy quote asset volume
  }
}
