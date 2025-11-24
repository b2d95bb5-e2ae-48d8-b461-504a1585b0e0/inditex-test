package com.inditex.zara.similarproducts.unit.adapter.in.web;

import com.inditex.zara.similarproducts.adapter.in.web.ProductApiControllerImpl;
import com.inditex.zara.similarproducts.application.service.SimilarProductsService;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApiControllerImplTest {

    @Mock
    SimilarProductsService service;

    @InjectMocks
    ProductApiControllerImpl controller;

    @Mock
    ServerWebExchange exchange; // not used by impl but required by signature

    @Test
    void getProductSimilar_shouldReturnOkWithBody() {
        // given
        String productId = "1";
        List<ProductDetail> details = List.of(
                new ProductDetail("2", "Dress", BigDecimal.valueOf(19.99), true),
                new ProductDetail("3", "Blazer", BigDecimal.valueOf(29.99), false)
        );

        when(service.getSimilarProducts(productId))
                .thenReturn(Mono.just(details));

        // when
        Mono<ResponseEntity<Flux<ProductDetail>>> monoResponse =
                controller.getProductSimilar(productId, exchange);

        // then
        StepVerifier.create(monoResponse)
                .assertNext(response -> {
                    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

                    List<ProductDetail> body = response.getBody()
                            .collectList()
                            .block();
                    assertThat(body).containsExactlyElementsOf(details);
                })
                .verifyComplete();
    }

    @Test
    void getProductSimilar_whenServiceEmitsEmptyMono_shouldReturn404() {
        // given
        String productId = "1";
        when(service.getSimilarProducts(productId))
                .thenReturn(Mono.empty());

        // when
        Mono<ResponseEntity<Flux<ProductDetail>>> monoResponse =
                controller.getProductSimilar(productId, exchange);

        // then
        StepVerifier.create(monoResponse)
                .assertNext(response ->
                        assertThat(response.getStatusCode().is4xxClientError()).isTrue()
                )
                .verifyComplete();
    }
}