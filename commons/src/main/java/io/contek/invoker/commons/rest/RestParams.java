package io.contek.invoker.commons.rest;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.joining;

public record RestParams(Map<String, Object> values) {

  private static final RestParams EMPTY = RestParams.newBuilder().build();

  public RestParams(Map<String, Object> values) {
    this.values = Collections.unmodifiableMap(values);
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static RestParams empty() {
    return EMPTY;
  }

  private static String toQueryString(Map<String, Object> params, Escaper escaper) {
    return params.entrySet().stream()
            .map(entry -> entry.getKey() + "=" + escaper.escape(entry.getValue().toString()))
            .collect(joining("&"));
  }

  public Builder toBuilder() {
    return newBuilder().addAll(values);
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public String getQueryString() {
    return getQueryString(Escapers.nullEscaper());
  }

  public String getQueryString(Escaper escaper) {
    return toQueryString(values, escaper);
  }

  public static final class Builder {

    private final Map<String, Object> values = new LinkedHashMap<>();

    private Builder() {
    }

    public Builder add(String key, long value) {
      values.put(key, value);
      return this;
    }

    public Builder add(String key, double value) {
      return add(key, BigDecimal.valueOf(value).toPlainString());
    }

    public Builder add(String key, boolean value) {
      values.put(key, value);
      return this;
    }

    public Builder add(String key, Long value) {
      values.put(key, value);
      return this;
    }

    public Builder add(String key, Double value) {
      if (value == null) {
        values.put(key, null);
        return this;
      }
      return add(key, BigDecimal.valueOf(value).toPlainString());
    }

    public Builder add(String key, Boolean value) {
      values.put(key, value);
      return this;
    }

    public Builder add(String key, String value) {
      values.put(key, value);
      return this;
    }

    public Builder addAll(Map<String, ?> values) {
      this.values.putAll(values);
      return this;
    }

    public RestParams build() {
      return build(false);
    }

    public RestParams build(boolean sort) {
      return !sort ? new RestParams(values) : new RestParams(new TreeMap<>(values));
    }
  }
}
