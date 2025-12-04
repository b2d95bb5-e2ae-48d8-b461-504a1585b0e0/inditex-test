package com.inditex.zara.similarproducts.infrastructure.webclient.customizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;

@Component
public class WiretapCustomizer implements HttpClientCustomizer {

    @Value("${http.wiretap:false}")
    private boolean enabled;

    @Override
    public HttpClient customize(HttpClient httpClient) {
        return enabled ? httpClient.wiretap(true) : httpClient;
    }
}
