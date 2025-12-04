package com.inditex.zara.similarproducts.infrastructure.cache.customizer;

import org.springframework.cache.caffeine.CaffeineCacheManager;

public interface CacheCustomizer {
    void customize(CaffeineCacheManager cacheManager);
}
