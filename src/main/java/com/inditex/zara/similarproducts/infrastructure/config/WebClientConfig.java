package com.inditex.zara.similarproducts.infrastructure.config;

import com.inditex.zara.similarproducts.infrastructure.webclient.customizer.HttpClientCustomizer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.List;

@Configuration
public class WebClientConfig {

    private final List<HttpClientCustomizer> customizers;

    public WebClientConfig(List<HttpClientCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Bean
    @Qualifier("externalApiWebClient")
    public WebClient webClient(@Value("${external.product-api.base-url}") String baseUrl) {

        ConnectionProvider provider = ConnectionProvider.builder("webclient-pool")
                .maxConnections(2000)
                .pendingAcquireMaxCount(2000)
                .pendingAcquireTimeout(Duration.ofSeconds(3))
                .build();

        HttpClient httpClient = HttpClient.create(provider);

        for (HttpClientCustomizer customizer : customizers) {
            httpClient = customizer.customize(httpClient);
        }

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
