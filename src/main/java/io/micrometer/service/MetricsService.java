package io.micrometer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import io.micrometer.model.CxfMetric;
import io.micrometer.model.MetricKey;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;

import java.util.Optional;

import static io.micrometer.cxf.CxfUtils.describeEndpoint;
import static io.micrometer.cxf.CxfUtils.extractOperation;
import static io.micrometer.cxf.CxfUtils.getExchange;
import static io.micrometer.cxf.CxfUtils.hasErrors;

public class MetricsService {
    private final MetricKey key;
    private final boolean failed;
    private final Exchange exchange;
    private final InitializationService initialize;

    private MetricsService(final Message message) {
        this.failed = hasErrors(message);
        this.exchange = getExchange(message);
        this.initialize = InitializationService.INSTANCE;
        this.key = new MetricKey(describeEndpoint(message), extractOperation(message));
    }

    public static MetricsService create(final Message message) {
        return new MetricsService(message);
    }

    public MetricsService startTimer(final CxfMetric metric) {
        initialize.findTimer(key, metric)
            .ifPresent(
                timer ->
                    exchange.put(
                        metric.name(),
                        Timer.start()
                    )
            );
        return this;
    }

    public MetricsService stopTimer(final CxfMetric metric) {
        initialize.findTimer(key, metric)
            .ifPresent(
                timer ->
                    Optional.ofNullable(exchange.get(metric.name()))
                        .filter(value -> Timer.Sample.class.isAssignableFrom(value.getClass()))
                        .map(sample -> (Timer.Sample) sample)
                        .ifPresent(sample -> sample.stop(timer))
            );
        return this;
    }

    public MetricsService inc(final CxfMetric metric) {
        this.initialize.findCounter(key, metric)
            .ifPresent(Counter::increment);
        return this;
    }

    public MetricsService incSuccess(final CxfMetric metric) {
        final MetricsService current;
        if (failed) {
            current = this;
        } else {
            current = inc(metric);
        }
        return current;
    }

    public MetricsService incErrors(final CxfMetric metric) {
        final MetricsService current;
        if (failed) {
            current = inc(metric);
        } else {
            current = this;
        }
        return current;
    }
}
