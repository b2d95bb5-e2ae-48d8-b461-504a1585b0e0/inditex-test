package com.inditex.zara.similarproducts.infrastructure.cache.customizer;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

@Component
public class MaxSizeCacheCustomizer implements CacheCustomizer {

    @Value("${cache.max-size:10000}")
    private long maxSize;

    @Override
    public void customize(CaffeineCacheManager cacheManager) {
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(maxSize)
        );
    }
}
