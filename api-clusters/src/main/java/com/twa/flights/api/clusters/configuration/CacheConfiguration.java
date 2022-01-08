package com.twa.flights.api.clusters.configuration;

import com.twa.flights.api.clusters.dto.ClusterSearchDTO;
import com.twa.flights.api.clusters.serializer.CatalogSerializer;
import com.twa.flights.api.clusters.serializer.ClusterSearchSerializer;
import com.twa.flights.api.clusters.serializer.JsonSerializer;
import com.twa.flights.api.clusters.serializer.StringSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.util.Collections;
import java.util.Set;

@Configuration
@EnableCaching
public class CacheConfiguration {

    public static final String CATALOG_CITY = "CATALOG_CITY";

    private static final Set<String> CACHE_NAMES = Collections.singleton(CATALOG_CITY);

    private final JsonSerializer jsonSerializer;

    public CacheConfiguration(JsonSerializer jsonSerializer) {
        this.jsonSerializer = jsonSerializer;
    }

    @Bean
    public CacheProperties cacheProperties() {
        return new CacheProperties();
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.fromSerializer(new StringSerializer()).getKeySerializationPair())
                .serializeValuesWith(RedisSerializationContext.fromSerializer(new CatalogSerializer(jsonSerializer))
                        .getValueSerializationPair())
                .entryTtl(cacheProperties.getRedis().getTimeToLive());
    }

    @Bean
    public RedisCacheManager cacheManager(RedisCacheConfiguration redisCacheConfiguration,
            RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder().cacheDefaults(redisCacheConfiguration).initialCacheNames(CACHE_NAMES)
                .cacheWriter(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory)).build();
    }

    @Bean
    public RedisTemplate<String, ClusterSearchDTO> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, ClusterSearchDTO> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringSerializer());
        redisTemplate.setValueSerializer(new ClusterSearchSerializer(jsonSerializer));
        return redisTemplate;
    }
}
