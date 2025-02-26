package io.contek.invoker.binancedelivery.api.websocket.user;

import io.contek.invoker.binancedelivery.api.websocket.common.WebSocketEventMessage;

import java.math.BigDecimal;
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

    @Override
    public String toString() {
      return "Message{" +
              "T=" + T +
              ", a=" + a +
              '}';
    }

    public static final class UpdateData {

      public String m; // event reason type
      public List<BalanceUpdate> B; // balance updates
      public List<PositionUpdate> P; // position updates

      @Override
      public String toString() {
        return "UpdateData{" +
                "m='" + m + '\'' +
                ", B=" + B +
                ", P=" + P +
                '}';
      }
    }
  }

  public static final class BalanceUpdate {

    public String a; // asset
    public BigDecimal wb; // wallet balance
    public BigDecimal cw; // cross wallet balance

    @Override
    public String toString() {
      return "BalanceUpdate{" +
              "a='" + a + '\'' +
              ", wb=" + wb +
              ", cw=" + cw +
              '}';
    }
  }

  public static final class PositionUpdate {

    public String s; // symbol
    public BigDecimal pa; // position amount
    public BigDecimal ep; // entry price
    public BigDecimal cr; // pre-fee accumulated realized
    public BigDecimal up; // unrealized PnL
    public String mt; // margin type
    public BigDecimal iw; // isolated wallet
    public String ps; // position side

    @Override
    public String toString() {
      return "PositionUpdate{" +
              "s='" + s + '\'' +
              ", pa=" + pa +
              ", ep=" + ep +
              ", cr=" + cr +
              ", up=" + up +
              ", mt='" + mt + '\'' +
              ", iw=" + iw +
              ", ps='" + ps + '\'' +
              '}';
    }
  }
}
