package io.contek.invoker.hbdmlinear.api.rest.user;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.actor.ratelimit.TypedPermitRequest;
import io.contek.invoker.commons.rest.RestContext;
import io.contek.invoker.commons.rest.RestParams;
import io.contek.invoker.hbdmlinear.api.common._LeverRate;
import io.contek.invoker.hbdmlinear.api.rest.common.RestDataResponse;

import static io.contek.invoker.hbdmlinear.api.ApiFactory.RateLimits.ONE_API_KEY_REST_PRIVATE_WRITE_REQUEST;
import static java.util.Objects.requireNonNull;

public final class PostSwapCrossSwitchLeverRate
    extends UserRestRequest<PostSwapCrossSwitchLeverRate.Response> {

  private String contract_code;
  private Integer lever_rate;

  PostSwapCrossSwitchLeverRate(IActor actor, RestContext context) {
    super(actor, context);
  }

  public PostSwapCrossSwitchLeverRate setContractCode(String contract_code) {
    this.contract_code = contract_code;
    return this;
  }

  public PostSwapCrossSwitchLeverRate setLeverRate(int lever_rate) {
    this.lever_rate = lever_rate;
    return this;
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected String getEndpointPath() {
    return "/linear-swap-api/v1/swap_cross_switch_lever_rate";
  }

  @Override
  protected RestParams getParams() {
    RestParams.Builder builder = RestParams.newBuilder();

    requireNonNull(contract_code);
    builder.add("contract_code", contract_code);

    requireNonNull(lever_rate);
    builder.add("lever_rate", lever_rate);

    return builder.build();
  }

  @Override
  protected ImmutableList<TypedPermitRequest> getRequiredQuotas() {
    return ONE_API_KEY_REST_PRIVATE_WRITE_REQUEST;
  }

  public static final class Response extends RestDataResponse<_LeverRate> {}
}
