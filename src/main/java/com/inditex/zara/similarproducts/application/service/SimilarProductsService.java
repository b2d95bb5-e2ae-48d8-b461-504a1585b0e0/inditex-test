package com.inditex.zara.similarproducts.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inditex.zara.similarproducts.adapter.client.ExternalApiClient;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService {

    private final ExternalApiClient external;

    @Autowired
    private ObjectMapper objectMapper;

    public SimilarProductsService(ExternalApiClient external) {
        this.external = external;
    }

    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return external.getSimilarProductIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(external::getProductDetail)
                .collectList();
    }
}
