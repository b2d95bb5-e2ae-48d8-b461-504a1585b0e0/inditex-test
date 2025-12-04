package com.inditex.zara.similarproducts.infrastructure.cache.customizer;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class ExpirationCacheCustomizer implements CacheCustomizer {

    @Value("${cache.expire-after-write-minutes:10}")
    private long expireAfterWrite;

    @Override
    public void customize(CaffeineCacheManager cacheManager) {
        cacheManager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(Duration.ofMinutes(expireAfterWrite))
        );
    }
}
