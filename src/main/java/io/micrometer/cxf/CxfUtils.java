package io.micrometer.cxf;

import io.micrometer.model.MetricKey;
import org.apache.cxf.binding.Binding;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.OperationInfo;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;

public final class CxfUtils {

    private CxfUtils() {
    }

    public static QName describeEndpoint(final Message message) {
        return findEndpointInfo(getExchange(message))
            .map(EndpointInfo::getName)
            .orElse(null);
    }

    public static QName extractOperation(final Message message) {
        return findBindings(getExchange(message))
            .map(BindingOperationInfo::getName)
            .orElse(null);
    }

    public static Set<MetricKey> extractKeys(final Endpoint endpoint) {
        final Optional<QName> info = Optional.ofNullable(endpoint.getEndpointInfo()).map(EndpointInfo::getName);
        return Optional.ofNullable(endpoint.getBinding())
            .map(Binding::getBindingInfo)
            .map(BindingInfo::getOperations)
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(ignored -> info.isPresent())
            .map(BindingOperationInfo::getOperationInfo)
            .map(OperationInfo::getName)
            .map(operation -> new MetricKey(info.get(), operation))
            .collect(toSet());
    }

    public static Exchange getExchange(final Message message) {
        return message.getExchange();
    }

    public static boolean hasErrors(final Message message) {
        return nonNull(message.getContent(Exception.class));
    }

    public static String explainQName(final QName name) {
        return Optional.ofNullable(name)
            .map(QName::getLocalPart)
            .orElse("unknown");
    }

    private static Optional<BindingOperationInfo> findBindings(final Exchange exchange) {
        return Optional.ofNullable(exchange.getBindingOperationInfo());
    }

    private static Optional<EndpointInfo> findEndpointInfo(final Exchange exchange) {
        return Optional.ofNullable(exchange.getEndpoint())
            .map(Endpoint::getEndpointInfo);
    }
}
