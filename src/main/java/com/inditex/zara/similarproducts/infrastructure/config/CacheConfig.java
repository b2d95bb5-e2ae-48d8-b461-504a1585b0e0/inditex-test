package com.inditex.zara.similarproducts.infrastructure.config;

import com.inditex.zara.similarproducts.infrastructure.cache.customizer.CacheCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    private final List<CacheCustomizer> customizers;

    public CacheConfig(List<CacheCustomizer> customizers) {
        this.customizers = customizers;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        customizers.forEach(c -> c.customize(cacheManager));

        return cacheManager;
    }
}


