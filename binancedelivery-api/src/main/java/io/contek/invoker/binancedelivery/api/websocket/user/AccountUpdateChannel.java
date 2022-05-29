package io.contek.invoker.binancedelivery.api.websocket.user;

import io.contek.invoker.binancedelivery.api.websocket.common.WebSocketEventMessage;

import java.util.List;

import static io.contek.invoker.binancedelivery.api.websocket.user.constants.UserEventTypeKeys._ACCOUNT_UPDATE;

public final class AccountUpdateChannel
    extends UserWebSocketChannel<AccountUpdateChannel.Id, AccountUpdateChannel.Message> {

  AccountUpdateChannel() {
    super(Id.INSTANCE);
  }

  @Override
  public Class<Message> getMessageType() {
    return Message.class;
  }

  public static final class Id extends UserWebSocketChannelId<Message> {

    private static final Id INSTANCE = new Id();

    private Id() {
      super(_ACCOUNT_UPDATE);
    }
  }

  public static final class Message extends WebSocketEventMessage {

    public Long T; // transaction
    public UpdateData a; // account update

    public static final class UpdateData {

      public String m; // event reason type
      public List<BalanceUpdate> B; // balance updates
      public List<PositionUpdate> P; // position updates
    }
  }

  public static final class BalanceUpdate {

    public String a; // asset
    public Double wb; // wallet balance
    public Double cw; // cross wallet balance
  }

  public static final class PositionUpdate {

    public String s; // symbol
    public Double pa; // position amount
    public Double ep; // entry price
    public Double cr; // pre-fee accumulated realized
    public Double up; // unrealized PnL
    public String mt; // margin type
    public Double iw; // isolated wallet
    public String ps; // position side
  }
}
