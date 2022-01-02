package com.twa.flights.api.provider.alpha.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twa.flights.api.provider.alpha.serializer.CitySerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String CATALOG_CITY = "CATALOG_CITY";

    private static final Set<String> CACHE_NAMES = Collections.singleton(CATALOG_CITY);

    private final ObjectMapper objectMapper;

    public CacheConfiguration(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Bean
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.fromSerializer(new StringRedisSerializer()).getKeySerializationPair())
                .serializeValuesWith(RedisSerializationContext.fromSerializer(new CitySerializer(objectMapper))
                        .getValueSerializationPair())
                .entryTtl(cacheProperties.getRedis().getTimeToLive());
    }

    @Bean
    public RedisCacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration,
            RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder().cacheDefaults(redisCacheConfiguration).initialCacheNames(CACHE_NAMES)
                .cacheWriter(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)).build();
    }
}
