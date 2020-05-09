package io.micrometer.model;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static io.micrometer.cxf.CxfUtils.explainQName;
import static java.util.stream.Collectors.toMap;

@Getter
public enum CxfMetric {
    TOTAL_MICROMETER_CXF_REQUEST_COUNTER(
        (key, registry) ->
            Counter
                .builder("cxf requests processed")
                .description("total number of processed cxf requests")
                .tag("endpoint", explainQName(key.getEndpoint()))
                .tag("operation", explainQName(key.getOperation()))
                .register(registry)
    ),
    SUCCESS_MICROMETER_CXF_REQUEST_COUNTER(
        (key, registry) ->
            Counter
                .builder("cxf requests success")
                .description("total number of successfully processed cxf requests")
                .tag("endpoint", explainQName(key.getEndpoint()))
                .tag("operation", explainQName(key.getOperation()))
                .register(registry)
    ),
    FAILED_MICROMETER_CXF_REQUEST_COUNTER(
        (key, registry) ->
            Counter
                .builder("cxf requests failed")
                .description("total number of failed cxf requests")
                .tag("endpoint", explainQName(key.getEndpoint()))
                .tag("operation", explainQName(key.getOperation()))
                .register(registry)
    ),
    CXF_MICROMETER_REQUEST_EXECUTION_TIME(
        (key, registry) ->
            Timer.builder("cxf_requests")
                .description("execution time of cxf request")
                .tag("endpoint", explainQName(key.getEndpoint()))
                .tag("operation", explainQName(key.getOperation()))
                .register(registry)
    );

    private final BiFunction<MetricKey, MeterRegistry, Meter> builder;

    CxfMetric(final BiFunction<MetricKey, MeterRegistry, Meter> metric) {
        this.builder = metric;
    }

    public static Map<CxfMetric, Optional<Meter>> createCxfMetricsMap() {
        return Arrays.stream(values())
            .collect(
                toMap(
                    Function.identity(),
                    value -> Optional.empty()
                )
            );
    }
}
