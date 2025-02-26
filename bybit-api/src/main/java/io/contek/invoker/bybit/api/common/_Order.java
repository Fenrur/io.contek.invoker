package io.contek.invoker.bybit.api.common;

public class _Order {

  public long user_id;
  public String order_id;
  public String symbol;
  public String side;
  public String order_type;
  public double price;
  public int qty;
  public String time_in_force;
  public String order_status;
  public String last_exec_time;
  public String last_exec_price;
  public int leaves_qty;
  public int cum_exec_qty;
  public double cum_exec_value;
  public double cum_exec_fee;
  public String reject_reason;
  public String order_link_id;
  public String created_at;
  public String updated_at;

  @Override
  public String toString() {
    return "_Order{" +
            "user_id=" + user_id +
            ", order_id='" + order_id + '\'' +
            ", symbol='" + symbol + '\'' +
            ", side='" + side + '\'' +
            ", order_type='" + order_type + '\'' +
            ", price=" + price +
            ", qty=" + qty +
            ", time_in_force='" + time_in_force + '\'' +
            ", order_status='" + order_status + '\'' +
            ", last_exec_time='" + last_exec_time + '\'' +
            ", last_exec_price='" + last_exec_price + '\'' +
            ", leaves_qty=" + leaves_qty +
            ", cum_exec_qty=" + cum_exec_qty +
            ", cum_exec_value=" + cum_exec_value +
            ", cum_exec_fee=" + cum_exec_fee +
            ", reject_reason='" + reject_reason + '\'' +
            ", order_link_id='" + order_link_id + '\'' +
            ", created_at='" + created_at + '\'' +
            ", updated_at='" + updated_at + '\'' +
            '}';
  }
}
