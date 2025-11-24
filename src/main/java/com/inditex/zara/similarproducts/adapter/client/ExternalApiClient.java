package com.inditex.zara.similarproducts.adapter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class ExternalApiClient {

    @Qualifier("externalApiWebClient")
    private final WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    public ExternalApiClient(@Qualifier("externalApiWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    //@Cacheable(cacheNames = "similarProductIds", key = "#productId")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackSimilarProductIds")
    @Retry(name = "externalApi")
    public Mono<List<String>> getSimilarProductIds(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Cacheable(cacheNames = "productDetails", key = "#productId")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "fallbackProductDetail")
    @Retry(name = "externalApi")
    public Mono<ProductDetail> getProductDetail(String productId) {
        return this.webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .timeout(Duration.ofSeconds(3))
                .onErrorResume(ex -> {
                    log.warn("Downstream failure for id {}: {}", productId, ex.getMessage());
                    return Mono.empty();
                });
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

