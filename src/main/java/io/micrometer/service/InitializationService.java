package io.micrometer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.model.CxfMetric;
import io.micrometer.model.MetricKey;
import org.apache.cxf.endpoint.Endpoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static io.micrometer.cxf.CxfUtils.extractKeys;
import static io.micrometer.model.CxfMetric.createCxfMetricsMap;
import static java.util.stream.Collectors.toMap;

public enum InitializationService {
    INSTANCE;

    private MeterRegistry registry;
    private Map<MetricKey, Map<CxfMetric, Optional<Meter>>> metrics;

    public void initialize(final MeterRegistry registry) {
        this.metrics = new HashMap<>();
        this.registry = Optional.ofNullable(registry)
            .orElseThrow(() -> new IllegalArgumentException("metric registry shouldn't be null"));
    }

    public void initializeMetrics(final Endpoint endpoint) {
        checkInitialized();
        Optional.ofNullable(endpoint)
            .ifPresent(
                e -> metrics.putAll(
                    extractKeys(e).stream()
                        .collect(
                            toMap(
                                Function.identity(),
                                ignored -> createCxfMetricsMap()
                            )
                        )
                )
            );
    }

    public Optional<Counter> findCounter(final MetricKey key, final CxfMetric metric) {
        return findMetric(
            key,
            Optional.ofNullable(getMetric(key, metric))
                .filter(value -> Counter.class.isAssignableFrom(value.getClass()))
                .map(counter -> (Counter) counter)
        );
    }

    public Optional<Timer> findTimer(final MetricKey key, final CxfMetric metric) {
        return findMetric(
            key,
            Optional.ofNullable(getMetric(key, metric))
                .filter(value -> Timer.class.isAssignableFrom(value.getClass()))
                .map(timer -> (Timer) timer)
        );
    }

    private <M extends Meter> Optional<M> findMetric(final MetricKey key, final Optional<M> metric) {
        return getMetrics(key).isEmpty() ? Optional.empty() : metric;
    }

    private Meter getMetric(final MetricKey key, final CxfMetric cxf) {
        checkInitialized();
        synchronized (getMetrics(key)) {
            return getMetrics(key).get(cxf)
                .orElseGet(() -> registerMetric(key, cxf));
        }
    }

    private Meter registerMetric(final MetricKey key, final CxfMetric metric) {
        checkInitialized();
        final Meter meter = metric.getBuilder().apply(key, this.registry);
        getMetrics(key).put(
            metric,
            Optional.of(meter)
        );
        return meter;
    }

    private Map<CxfMetric, Optional<Meter>> getMetrics(final MetricKey key) {
        return Optional.ofNullable(this.metrics.get(key)).orElseGet(Collections::emptyMap);
    }

    private void checkInitialized() {
        Optional.ofNullable(this.metrics)
            .orElseThrow(() -> new IllegalArgumentException("cxf micrometer metrics are not initialized."));
    }
}
