package com.inditex.zara.similarproducts.infrastructure.webclient.customizer;

import reactor.netty.http.client.HttpClient;

public interface HttpClientCustomizer {
    HttpClient customize(HttpClient httpClient);
}
