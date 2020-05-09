# CXF metrics for micrometer

Collects the following metrics for each endpoint and operation 

    cxf_requests_processed_total - total number of cxf requests ,
    cxf_requests_seconds_sum     - total execution time of cxf requests,
    cxf_requests_seconds_max     - maximum execution time of cxf request,
    cxf_requests_success_total   - total number of successfully processed cxf requests,
    cxf_requests_failed_total    - total number of failed cxf requests

For example, for configuration bellow

    @Bean
    public Endpoint server1(final Bus bus, final MyFirstWebService service) {
        final EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.setEndpointName(new QName("server1"));
        endpoint.publish("/server1");
        return endpoint;
    }

    @Bean
    public Endpoint server2(final Bus bus, final MySecondWebService service) {
        final EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.setEndpointName(new QName("server2"));
        endpoint.publish("/server2");
        return endpoint;
    }
    
    @Bean
    public MyCxfClient client(final @Value("${my.cxf.client.url}") String url) {
        final JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setAddress(url);
        factory.setEndpointName(new QName("client"));
        return factory.create(MyCxfClient.class);
    }

the cxf_requests_success_total metric will be available for 'server1' endpoint as 'cxf_requests_success_total{endpoint="server1",operation="method1",}' for each web service method of MyFirstWebService.  
And the same for other metrics of MyFirstWebService, MySecondWebService and MyCxfClient.

To enable cxf metrics for micrometer add dependency to your pom.xml.

    <dependency>
        <groupId>io.github.kdprog</groupId>
        <artifactId>cxf-micrometer-metrics</artifactId>
        <version>1.0.0</version>
    </dependency>
    
For spring applications add the following bean to your application configuration.

    @Bean
    public FactoryBeanListener cxfMicrometerBean(final MeterRegistry registry) {
        return new MicrometerFactoryBeanListener(registry);
    }