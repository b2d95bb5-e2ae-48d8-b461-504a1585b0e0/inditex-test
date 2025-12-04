package com.inditex.zara.similarproducts.application.port.in;

import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GetSimilarProductsQuery {
    Mono<List<ProductDetail>> getSimilarProducts(String productId);
}
