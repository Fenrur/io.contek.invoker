package io.contek.invoker.bybit.api.websocket.market;

import io.contek.invoker.bybit.api.websocket.WebSocketChannel;
import io.contek.invoker.bybit.api.websocket.WebSocketChannelId;
import io.contek.invoker.bybit.api.websocket.common.WebSocketTopicMessage;

import java.util.List;

public final class KlineV2Channel
    extends WebSocketChannel<KlineV2Channel.Id, KlineV2Channel.Message> {

  KlineV2Channel(KlineV2Channel.Id id) {
    super(id);
  }

  @Override
  public Class<Message> getMessageType() {
    return Message.class;
  }

  public static final class Id extends WebSocketChannelId<KlineV2Channel.Message> {

    private Id(String topic) {
      super(topic);
    }

    public static Id of(String interval, String symbol) {
      return new Id(String.format("klineV2.%s.%s", interval, symbol));
    }
  }

  public static final class Message extends WebSocketTopicMessage {

    public List<KlineV2> data;
    public Long timestamp_e6;

    @Override
    public String toString() {
      return "Message{" +
              "data=" + data +
              ", timestamp_e6=" + timestamp_e6 +
              '}';
    }
  }

  public static final class KlineV2 {

    public Long start; // Start timestamp point for result, in seconds
    public Long end; // End timestamp point for result, in seconds
    public Double open; // Starting price
    public Double close; // Closing price
    public Double high; // Maximum price
    public Double low; // Minimum price
    public Double volume; // Trading volume
    public Double turnover; // Transaction amount
    public Boolean confirm; // Is confirm
    public Long cross_seq; // Cross sequence (internal value)
    public Long timestamp; // End timestamp point for result, in seconds

    @Override
    public String toString() {
      return "KlineV2{" +
              "start=" + start +
              ", end=" + end +
              ", open=" + open +
              ", close=" + close +
              ", high=" + high +
              ", low=" + low +
              ", volume=" + volume +
              ", turnover=" + turnover +
              ", confirm=" + confirm +
              ", cross_seq=" + cross_seq +
              ", timestamp=" + timestamp +
              '}';
    }
  }
}
