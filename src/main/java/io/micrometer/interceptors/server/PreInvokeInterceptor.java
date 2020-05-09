package io.micrometer.interceptors.server;

import io.micrometer.service.MetricsService;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import static io.micrometer.model.CxfMetric.CXF_MICROMETER_REQUEST_EXECUTION_TIME;

public class PreInvokeInterceptor extends AbstractPhaseInterceptor<Message> {

    public PreInvokeInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        MetricsService
            .create(message)
            .startTimer(CXF_MICROMETER_REQUEST_EXECUTION_TIME);
    }
}
