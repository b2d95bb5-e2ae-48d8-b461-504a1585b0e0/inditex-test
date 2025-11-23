package com.inditex.zara.similarproducts.infrastructure.in.web;

import com.inditex.zara.similarproducts.application.service.SimilarProductsService;
import com.inditex.zara.similarproducts.domain.model.ProductDetail;
import com.inditex.zara.similarproducts.infrastructure.adapter.in.web.ProductApiController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ProductApiControllerImpl extends ProductApiController {

    private final SimilarProductsService service;

    public ProductApiControllerImpl(SimilarProductsService service) {
        this.service = service;
    }

    @Override
    public Mono<ResponseEntity<Flux<ProductDetail>>> getProductSimilar(
            String productId,
            ServerWebExchange exchange
    ) {
        return service.getSimilarProducts(productId)
                .map(list -> ResponseEntity.ok(Flux.fromIterable(list)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
