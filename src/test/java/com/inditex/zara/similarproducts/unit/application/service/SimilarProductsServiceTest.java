package com.inditex.zara.similarproducts.unit.application.service;

import com.inditex.zara.similarproducts.application.service.SimilarProductsService;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import com.inditex.zara.similarproducts.infrastructure.adapter.out.externalproductapi.ExternalApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimilarProductsServiceTest {

    @Mock
    ExternalApiClient externalApiClient;

    @InjectMocks
    SimilarProductsService service;

    @Test
    void getSimilarProducts_shouldReturnDetailsForAllSimilarIds() {
        // given
        String baseProductId = "1";
        List<String> similarIds = List.of("2", "3");

        ProductDetail p2 = new ProductDetail("2", "Dress", BigDecimal.valueOf(19.99), true);
        ProductDetail p3 = new ProductDetail("3", "Blazer", BigDecimal.valueOf(29.99), false);

        when(externalApiClient.getSimilarProductIds(baseProductId))
                .thenReturn(Mono.just(similarIds));
        when(externalApiClient.getProductDetail("2"))
                .thenReturn(Mono.just(p2));
        when(externalApiClient.getProductDetail("3"))
                .thenReturn(Mono.just(p3));

        // when
        Mono<List<ProductDetail>> result = service.getSimilarProducts(baseProductId);

        // then
        StepVerifier.create(result)
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list).containsExactlyInAnyOrder(p2, p3);
                })
                .verifyComplete();
    }

    @Test
    void getSimilarProducts_whenNoSimilarIds_shouldReturnEmptyList() {
        // given
        String baseProductId = "1";
        when(externalApiClient.getSimilarProductIds(baseProductId))
                .thenReturn(Mono.just(List.of()));

        // when
        Mono<List<ProductDetail>> result = service.getSimilarProducts(baseProductId);

        // then
        StepVerifier.create(result)
                .assertNext(list -> assertThat(list).isEmpty())
                .verifyComplete();
    }
}