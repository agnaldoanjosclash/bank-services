package com.home.account.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String CLIENT_CACHE = "CLIENT_CACHE";

    private final Long ttl;

    public CacheConfiguration(@Value("${client.feign.registration.cache-ttl}") final Long ttl) {
        this.ttl = ttl;
    }

    @Bean
    public CaffeineCache clientCache() {
        CaffeineCache cacheManager = new CaffeineCache(CLIENT_CACHE,
                Caffeine.newBuilder().expireAfterWrite(ttl, TimeUnit.MINUTES).build()
        );
        return cacheManager;
    }

}
