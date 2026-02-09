package com.example.online.config;

import com.example.online.upload.service.impl.UploadImageService;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

@Configuration
public class RedisConfig {
    public static final String RATE_LIMIT_CACHE = "rate-limit-buckets";
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfig.class);

    // Global cache Manager for backend server (org.springframework.data.redis.cache.RedisCacheManager)
    @Bean
    @Primary //Thêm cho chắc
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){

        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(RedisSerializer.json()));
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    // Redisson Client
    @Bean
    public Config redissonConfig(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return config;
    }

    // cache Manager for Redisson client for bucket4j
    @Bean
    public CacheManager jcacheManagerBucket4j(Config config){
        // Looking in every Java Redis client that supported jcache API, choose redisson
        CachingProvider provider = Caching.getCachingProvider("org.redisson.jcache.JCachingProvider");

        // Get manager from provider
        CacheManager cacheManager = provider.getCacheManager();

        // Set cache config using Redisson config
        javax.cache.configuration.Configuration<Object, Object> jcacheConfig =
                 org.redisson.jcache.configuration.RedissonConfiguration.fromConfig(config);

        // Chủ động tạo cache trước. Nếu không, filter đầu tiên truy cập
        // có thể gặp lỗi "cache not found" trong môi trường đa luồng.
        if (cacheManager.getCache(RATE_LIMIT_CACHE) == null){
            cacheManager.createCache(RATE_LIMIT_CACHE, jcacheConfig);
        }
        LOG.info("JCache API {} for bucket4j initialized.", RATE_LIMIT_CACHE);
        return cacheManager;
    }
}
