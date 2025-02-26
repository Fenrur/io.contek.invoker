package io.contek.invoker.ftx.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.ftx.api.common._LendingInfo;
import io.contek.invoker.ftx.api.rest.common.RestResponse;

import java.util.List;

import static io.contek.invoker.ftx.api.ApiFactory.RateLimits.ONE_REST_REQUEST;

public final class GetLendingInfo extends UserRestRequest<GetLendingInfo.Response> {

  GetLendingInfo(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_REST_REQUEST;
  }

  @Override
  protected Class<GetLendingInfo.Response> getResponseType() {
    return GetLendingInfo.Response.class;
  }

  @Override
  protected RestMethod getMethod() {
    return RestMethod.GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/api/spot_margin/lending_info";
  }

  @Override
  protected RestParams getParams() {
    return RestParams.empty();
  }

  public static final class Response extends RestResponse<List<_LendingInfo>> {}
}
