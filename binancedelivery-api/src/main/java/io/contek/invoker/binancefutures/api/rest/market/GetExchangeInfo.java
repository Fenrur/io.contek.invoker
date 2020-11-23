package io.contek.invoker.binancefutures.api.rest.market;

import com.google.common.collect.ImmutableList;
import io.contek.invoker.binancefutures.api.rest.market.GetExchangeInfo.Response;
import io.contek.invoker.commons.api.actor.IActor;
import io.contek.invoker.commons.api.actor.ratelimit.RateLimitQuota;
import io.contek.invoker.commons.api.rest.RestContext;
import io.contek.invoker.commons.api.rest.RestParams;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.List;
import java.util.Map;

import static io.contek.invoker.binancefutures.api.ApiFactory.RateLimits.ONE_REST_REQUEST;

@NotThreadSafe
public final class GetExchangeInfo extends MarketRestRequest<Response> {

  GetExchangeInfo(IActor actor, RestContext context) {
    super(actor, context);
  }

  @Override
  protected Class<Response> getResponseType() {
    return Response.class;
  }

  @Override
  protected String getEndpointPath() {
    return "/dapi/v1/exchangeInfo";
  }

  @Override
  protected RestParams getParams() {
    return RestParams.empty();
  }

  @Override
  protected ImmutableList<RateLimitQuota> getRequiredQuotas() {
    return ONE_REST_REQUEST;
  }

  @NotThreadSafe
  public static final class Response {

    public List<MarketDetails> symbols;
  }

  @NotThreadSafe
  public static final class MarketDetails {

    public String symbol;
    public String pair;
    public String contractType;
    public Long deliveryDate;
    public Long onboardDate;
    public String contractStatus;
    public Double contractSize;
    public String quoteAsset;
    public String baseAsset;
    public String marginAsset;
    public Integer pricePrecision;
    public Integer quantityPrecision;
    public Integer baseAssetPrecision;
    public Integer quotePrecision;
    public String underlyingType;
    public List<Map<String, Object>> filters;
  }
}
