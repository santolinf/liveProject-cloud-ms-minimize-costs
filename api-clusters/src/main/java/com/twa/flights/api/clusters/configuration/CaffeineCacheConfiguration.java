package com.twa.flights.api.clusters.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableCaching
public class CaffeineCacheConfiguration {

    private static final Collection<String> CACHE_NAMES = Collections.singleton("city");

    @Bean
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

    @Bean
    public Caffeine<?, ?> caffeine(CacheProperties cacheProperties) {
        return Caffeine.from(cacheProperties.getCaffeine().getSpec());
    }

    @Bean
    public CacheManager caffeineCacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCacheNames(CACHE_NAMES);
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}
