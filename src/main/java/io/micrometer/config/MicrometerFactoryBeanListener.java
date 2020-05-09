package io.micrometer.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.feature.MicrometerMetricsFeature;
import io.micrometer.service.InitializationService;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.service.factory.AbstractServiceFactoryBean;
import org.apache.cxf.service.factory.FactoryBeanListener;

import static org.apache.cxf.service.factory.FactoryBeanListener.Event.CLIENT_CREATED;
import static org.apache.cxf.service.factory.FactoryBeanListener.Event.SERVER_CREATED;

public class MicrometerFactoryBeanListener implements FactoryBeanListener {
    private final InitializationService initialize;
    private final MicrometerMetricsFeature feature;

    public MicrometerFactoryBeanListener(final MeterRegistry registry) {
        this.feature = new MicrometerMetricsFeature();
        this.initialize = InitializationService.INSTANCE;
        this.initialize.initialize(registry);
    }

    @Override
    public void handleEvent(final Event ev, final AbstractServiceFactoryBean factory, final Object... args) {
        if (ev == SERVER_CREATED) {
            final Server server = (Server) args[0];
            this.initialize.initializeMetrics(server.getEndpoint());
            feature.initialize(server, factory.getBus());
        } else if (ev == CLIENT_CREATED) {
            this.initialize.initializeMetrics((Endpoint) args[1]);
            feature.initialize((Client) args[0], factory.getBus());
        }
    }
}
