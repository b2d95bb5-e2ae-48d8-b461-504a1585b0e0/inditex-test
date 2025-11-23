package com.inditex.zara.similarproducts.adapter.client;

import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.netty.channel.ChannelOption;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
public class ExternalApiClient {

    private final WebClient webClient;

    public ExternalApiClient(WebClient.Builder builder,
                             @Value("${external.product-api.base-url}") String baseUrl) {

        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 2000)
                .responseTimeout(Duration.ofSeconds(2))
                .compress(true)
                .keepAlive(true)
                .secure()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .proxy(proxy -> {})
                .metrics(true, s -> s)
                .wiretap(true);

        ConnectionProvider provider = ConnectionProvider.builder("webclient-pool")
                .maxConnections(2000)
                .pendingAcquireMaxCount(2000)
                .pendingAcquireTimeout(Duration.ofSeconds(2))
                .build();

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create(provider))).build();


    }

    @Cacheable(cacheNames = "similarProductIds", key = "#productId")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackSimilarProductIds")
    @Retry(name = "externalApi")
    public Mono<List<String>> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .collectList();
    }

    @Cacheable(cacheNames = "productDetails", key = "#productId")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackProductDetail")
    @Retry(name = "externalApi")
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDetail.class);
    }

    // ---- Fallbacks ----

    private Mono<List<String>> fallbackSimilarProductIds(String productId, Throwable ex) {
        // Degrade gracefully: no similar products if external is down
        return Mono.just(Collections.emptyList());
    }

    private Mono<ProductDetail> fallbackProductDetail(String productId, Throwable ex) {
        // You can choose to return a "minimal" product or just propagate error
        return Mono.error(new IllegalStateException(
                "Product details not available for id " + productId, ex));
    }

}

