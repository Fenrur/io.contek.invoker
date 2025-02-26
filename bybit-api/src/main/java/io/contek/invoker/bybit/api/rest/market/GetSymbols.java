package io.contek.invoker.bybit.api.rest.market;

import io.contek.invoker.bybit.api.common._Symbol;
import io.contek.invoker.bybit.api.rest.common.RestResponse;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestParams;

import java.util.List;

import static io.contek.invoker.bybit.api.rest.market.GetSymbols.Response;

public final class GetSymbols extends MarketRestRequest<Response> {

  GetSymbols(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected String getEndpointPath() {
    return "/v2/public/symbols";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();
    return builder.build();
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  public static final class Response extends RestResponse<List<_Symbol>> {}
}
