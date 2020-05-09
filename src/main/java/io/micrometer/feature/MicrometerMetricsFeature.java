package io.micrometer.feature;

import io.micrometer.interceptors.client.FaultInInterceptor;
import io.micrometer.interceptors.client.PostInvokeInterceptor;
import io.micrometer.interceptors.client.SetupInterceptor;
import io.micrometer.interceptors.server.FaultOutInterceptor;
import io.micrometer.interceptors.server.PreInvokeInterceptor;
import io.micrometer.interceptors.server.SendInterceptor;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;

public class MicrometerMetricsFeature extends AbstractFeature {

    @Override
    public void initialize(final Client client, final Bus bus) {
        client.getOutInterceptors().add(new SetupInterceptor());
        client.getInFaultInterceptors().add(new FaultInInterceptor());
        client.getInInterceptors().add(new PostInvokeInterceptor());
    }

    @Override
    public void initialize(final Server server, final Bus bus) {
        final Endpoint endpoint = server.getEndpoint();
        endpoint.getOutInterceptors().add(new SendInterceptor());
        endpoint.getOutFaultInterceptors().add(new FaultOutInterceptor());
        endpoint.getInInterceptors().add(new PreInvokeInterceptor());
    }
}
