package io.contek.invoker.bybitlinear.api.websocket.user;

import io.contek.invoker.bybitlinear.api.websocket.WebSocketChannel;
import io.contek.invoker.bybitlinear.api.websocket.WebSocketChannelId;
import io.contek.invoker.bybitlinear.api.websocket.common.WebSocketTopicMessage;

import java.util.List;

public final class PositionChannel
    extends WebSocketChannel<PositionChannel.Id, PositionChannel.Message> {

  PositionChannel() {
    super(Id.INSTANCE);
  }

  @Override
  public Class<Message> getMessageType() {
    return Message.class;
  }

  public static final class Id extends WebSocketChannelId<Message> {

    private static final Id INSTANCE = new Id();

    private Id() {
      super("position");
    }
  }

  public static final class Message extends WebSocketTopicMessage {

    public List<Data> data;
  }

  public static final class Data {

    public Long user_id; // user ID
    public String symbol; // the contract for this position
    public Double size; // the current position amount
    public String side; // side
    public String position_value; // positional value
    public String entry_price; // entry price
    public String liq_price; // liquidation price
    public String bust_price; // bankruptcy price
    public String leverage; // leverage
    public String order_margin; // order margin
    public String position_margin; // position margin
    public String occ_closing_fee; // position closing
    public String take_profit; // take profit price
    public String
        tp_trigger_by; // take profit trigger price, eg: LastPrice, IndexPrice. Conditional order
    // only
    public String stop_loss; // stop loss price
    public String
        sl_trigger_by; // stop loss trigger price, eg: LastPrice, IndexPrice. Conditional order only
    public String realised_pnl; // realised PNL
    public String cum_realised_pnl; // Total realized profit and loss
    public Long position_seq; // position version number
  }
}
