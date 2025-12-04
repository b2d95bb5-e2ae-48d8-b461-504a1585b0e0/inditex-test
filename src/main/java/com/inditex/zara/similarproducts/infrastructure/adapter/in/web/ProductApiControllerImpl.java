package com.inditex.zara.similarproducts.infrastructure.adapter.in.web;

import com.inditex.zara.similarproducts.adapter.in.web.ProductApi;
import com.inditex.zara.similarproducts.application.port.in.GetSimilarProductsQuery;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductApiControllerImpl implements ProductApi {

    private final GetSimilarProductsQuery getSimilarProductsQuery;

    public ProductApiControllerImpl(GetSimilarProductsQuery getSimilarProductsQuery) {
        this.getSimilarProductsQuery = getSimilarProductsQuery;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductDetail>>> getProductSimilar(
            String productId,
            ServerWebExchange exchange
    ) {
        return getSimilarProductsQuery.getSimilarProducts(productId)
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
