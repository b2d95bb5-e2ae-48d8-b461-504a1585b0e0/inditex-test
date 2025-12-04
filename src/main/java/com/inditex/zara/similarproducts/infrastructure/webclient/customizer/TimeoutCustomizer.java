package com.inditex.zara.similarproducts.infrastructure.webclient.customizer;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class TimeoutCustomizer implements HttpClientCustomizer {

    @Value("${http.timeout:3000}")
    private int timeout;

    @Override
    public HttpClient customize(HttpClient httpClient) {
        return httpClient
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .responseTimeout(Duration.ofMillis(timeout));
    }
}

