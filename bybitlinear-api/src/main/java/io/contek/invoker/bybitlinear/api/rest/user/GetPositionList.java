package io.contek.invoker.bybitlinear.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.bybitlinear.api.common._Position;
import io.contek.invoker.bybitlinear.api.rest.common.RestResponse;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestMethod;
import io.contek.invoker.commons.rest.RestParams;

import java.util.List;

import static io.contek.invoker.bybitlinear.api.ApiFactory.RateLimits.ONE_REST_PRIVATE_POSITION_READ_REQUEST;
import static io.contek.invoker.bybitlinear.api.rest.user.GetPositionList.Response;
import static io.contek.invoker.commons.rest.RestMethod.GET;

public final class GetPositionList extends UserRestRequest<Response> {

  GetPositionList(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected RestMethod getMethod() {
    return GET;
  }

  @Override
  protected String getEndpointPath() {
    return "/private/linear/position/list";
  }

  @Override
  protected RestParams getParams() {
    return RestParams.empty();
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_REST_PRIVATE_POSITION_READ_REQUEST;
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  public static final class Response extends RestResponse<List<Result>> {}

  public static final class Result {

    public boolean is_valid;
    public _Position data;
  }
}
