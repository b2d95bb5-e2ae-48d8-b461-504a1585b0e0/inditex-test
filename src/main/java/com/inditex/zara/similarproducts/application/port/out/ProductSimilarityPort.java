package com.inditex.zara.similarproducts.application.port.out;

import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductSimilarityPort {

    Mono<List<String>> getSimilarProductIds(String productId);

    Mono<ProductDetail> getProductDetail(String productId);
}

