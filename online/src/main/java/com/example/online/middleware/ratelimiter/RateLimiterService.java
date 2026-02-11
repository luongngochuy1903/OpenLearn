package com.example.online.middleware.ratelimiter;

import java.time.Duration;

public interface RateLimiterService {
    public boolean allowedRequest(String key,
                                  long capacity,
                                  long refillTokens,
                                  int duration);
}
