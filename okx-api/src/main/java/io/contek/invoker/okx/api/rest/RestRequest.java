package io.contek.invoker.okx.api.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.net.UrlEscapers;
import io.contek.invoker.commons.actor.IActor;
import io.contek.invoker.commons.rest.*;
import io.contek.invoker.security.ICredential;
import io.vertx.core.buffer.Buffer;

import java.time.Clock;
import java.time.format.DateTimeFormatter;

import static io.contek.invoker.commons.rest.RestMediaType.JSON;
import static java.time.ZoneOffset.UTC;
import static java.time.format.DateTimeFormatter.ISO_INSTANT;

public abstract class RestRequest<R> extends BaseRestRequest<R> {

  public static final String OK_ACCESS_PASSPHRASE = "OK-ACCESS-PASSPHRASE";

  private static final DateTimeFormatter FORMATTER = ISO_INSTANT.withZone(UTC);

  private final RestContext context;
  private final Clock clock;

  protected RestRequest(IActor actor, RestContext context) {
    super(actor);
    this.context = context;
    clock = actor.clock();
  }

  protected abstract RestMethod getMethod();

  protected abstract String getEndpointPath();

  protected abstract RestParams getParams();

  @Override
  protected final RestCall createCall(ICredential credential) {
    RestMethod method = getMethod();
    return switch (method) {
      case GET -> {
        String paramsString = buildParamsString();
        yield RestCall.newBuilder()
          .setUrl(buildUrlString(paramsString))
          .setMethod(method)
          .setHeaders(generateHeaders(paramsString, Buffer.buffer(""), credential))
          .build();
      }
      case POST -> {
        RestMediaBody body = JSON.create(getParams());
        yield RestCall.newBuilder()
          .setUrl(buildUrlString(""))
          .setMethod(method)
          .setHeaders(generateHeaders("", body.body(), credential))
          .setBody(body)
          .build();
      }
      default -> throw new IllegalStateException(getMethod().name());
    };
  }

  private ImmutableMap<String, String> generateHeaders(
      String paramsString, Buffer body, ICredential credential) {
    if (credential.isAnonymous()) {
      return ImmutableMap.of();
    }
    String ts = FORMATTER.format(clock.instant());
    String payload = ts + getMethod() + getEndpointPath() + paramsString + body.toString();
    String signature = credential.sign(payload);

    ImmutableMap.Builder<String, String> result =
        ImmutableMap.<String, String>builder()
            .put("OK-ACCESS-KEY", credential.getApiKeyId())
            .put("OK-ACCESS-SIGN", signature)
            .put("OK-ACCESS-TIMESTAMP", ts);
    credential.getProperties().forEach(result::put);

    return result.build();
  }

  private String buildParamsString() {
    RestParams params = getParams();
    if (params.isEmpty()) {
      return "";
    }
    return "?" + params.getQueryString(UrlEscapers.urlPathSegmentEscaper());
  }

  private String buildUrlString(String paramsString) {
    return context.baseUrl() + getEndpointPath() + paramsString;
  }
}
