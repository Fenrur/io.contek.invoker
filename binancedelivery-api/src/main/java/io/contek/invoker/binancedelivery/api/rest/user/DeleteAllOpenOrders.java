package io.contek.invoker.binancedelivery.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.binancedelivery.api.common._Order;
import io.contek.invoker.binancedelivery.api.rest.user.DeleteAllOpenOrders.Response;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;

import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.contek.invoker.binancedelivery.api.ApiFactory.RateLimits.ONE_REST_REQUEST;
import static io.contek.invoker.commons.rest.RestMethod.DELETE;

public final class DeleteAllOpenOrders extends UserRestRequest<Response> {

  private String symbol;

  DeleteAllOpenOrders(IActor actor, RestContext context) {
    super(actor, context);
  }

  public DeleteAllOpenOrders setSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return DELETE;
  }

  @Override
  protected String getEndpointPath() {
    return "/dapi/v1/allOpenOrders";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    checkNotNull(symbol);
    builder.add("symbol", symbol);

    builder.add("timestamp", getMillis());

    return builder.build();
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_REST_REQUEST;
  }

  public static final class Response extends ArrayList<_Order> {}
}
