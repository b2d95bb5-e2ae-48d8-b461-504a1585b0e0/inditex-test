package com.inditex.zara.similarproducts.application.service;

import com.inditex.zara.similarproducts.application.port.in.GetSimilarProductsQuery;
import com.inditex.zara.similarproducts.application.port.out.ProductSimilarityPort;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SimilarProductsService implements GetSimilarProductsQuery {

    private final ProductSimilarityPort similarityPort;

    public SimilarProductsService(ProductSimilarityPort similarityPort) {
        this.similarityPort = similarityPort;
    }

    @Override
    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return similarityPort.getSimilarProductIds(productId)
                .flatMapMany(Flux::fromIterable)
                .flatMap(similarityPort::getProductDetail)
                .collectList();
    }
}
