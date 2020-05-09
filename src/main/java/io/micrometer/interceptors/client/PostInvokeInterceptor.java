package io.micrometer.interceptors.client;

import io.micrometer.service.MetricsService;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import static io.micrometer.model.CxfMetric.CXF_MICROMETER_REQUEST_EXECUTION_TIME;
import static io.micrometer.model.CxfMetric.FAILED_MICROMETER_CXF_REQUEST_COUNTER;
import static io.micrometer.model.CxfMetric.SUCCESS_MICROMETER_CXF_REQUEST_COUNTER;
import static io.micrometer.model.CxfMetric.TOTAL_MICROMETER_CXF_REQUEST_COUNTER;

public class PostInvokeInterceptor extends AbstractPhaseInterceptor<Message> {

    public PostInvokeInterceptor() {
        super(Phase.POST_INVOKE);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        MetricsService
            .create(message)
            .inc(TOTAL_MICROMETER_CXF_REQUEST_COUNTER)
            .incSuccess(SUCCESS_MICROMETER_CXF_REQUEST_COUNTER)
            .incErrors(FAILED_MICROMETER_CXF_REQUEST_COUNTER)
            .stopTimer(CXF_MICROMETER_REQUEST_EXECUTION_TIME);
    }
}
