package com.inditex.zara.similarproducts.application.service;

import com.inditex.zara.similarproducts.adapter.client.ExternalApiClient;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService {

    private final ExternalApiClient external;

    public SimilarProductsService(ExternalApiClient external) {
        this.external = external;
    }

    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return external.getSimilarProductIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(external::getProductDetail, 10) // max 10 concurrent calls
                .collectList();
    }
}
