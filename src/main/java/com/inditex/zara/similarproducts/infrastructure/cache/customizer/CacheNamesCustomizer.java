package com.inditex.zara.similarproducts.infrastructure.cache.customizer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CacheNamesCustomizer implements CacheCustomizer {

    @Value("${cache.names}")
    private List<String> cacheNames;

    @Override
    public void customize(CaffeineCacheManager cacheManager) {
        cacheManager.setCacheNames(cacheNames);
    }
}
