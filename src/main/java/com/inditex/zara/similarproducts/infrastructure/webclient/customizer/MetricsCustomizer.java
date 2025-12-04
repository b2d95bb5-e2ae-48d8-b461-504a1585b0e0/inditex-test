package com.inditex.zara.similarproducts.infrastructure.webclient.customizer;

import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;

@Component
public class MetricsCustomizer implements HttpClientCustomizer {

    @Override
    public HttpClient customize(HttpClient httpClient) {
        return httpClient.metrics(true, s -> s);
    }
}
