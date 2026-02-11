package com.example.online.middleware.ratelimiter.Impl;

import com.example.online.config.RedisConfig;
import com.example.online.middleware.ratelimiter.RateLimiterService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.grid.jcache.JCacheProxyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.cache.Cache;
import javax.cache.CacheManager;
import java.time.Duration;

@Service
public class RateLimiterServiceImpl implements RateLimiterService {

    private final ProxyManager<String> proxyManager;
    private static final Logger LOG = LoggerFactory.getLogger(RateLimiterServiceImpl.class);

    public RateLimiterServiceImpl(CacheManager jcacheManager) {
        Cache<String, byte[]> cache =
                jcacheManager.getCache(RedisConfig.RATE_LIMIT_CACHE);
        this.proxyManager = new JCacheProxyManager<>(cache);
    }

    public boolean allowedRequest(String key,
                                  long capacity,
                                  long refillTokens,
                                  int duration) {
        System.out.println("Going in allowed Request");
        BucketConfiguration configuration =
                BucketConfiguration.builder()
                        .addLimit(Bandwidth.builder()
                                .capacity(capacity)
                                .refillGreedy(refillTokens, Duration.ofMinutes(duration))
                                .build())
                        .build();

        Bucket bucket = proxyManager.builder()
                .build(key, () -> configuration);

//        return bucket.tryConsume(1);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        System.out.println("Consumed: " + probe.isConsumed());
        System.out.println("Remaining: " + probe.getRemainingTokens());

        return probe.isConsumed();
    }
}
