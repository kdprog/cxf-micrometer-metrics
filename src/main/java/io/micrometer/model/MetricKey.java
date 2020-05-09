package io.micrometer.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.xml.namespace.QName;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class MetricKey {
    private final QName endpoint;
    private final QName operation;
}
