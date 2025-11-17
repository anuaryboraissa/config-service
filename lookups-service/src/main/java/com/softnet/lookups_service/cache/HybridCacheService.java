package com.softnet.lookups_service.cache;


import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Service
public class HybridCacheService implements CacheService {

    private final Cache<String, String> localCache;
    private final RedisTemplate<String, String> redisTemplate;

    public HybridCacheService(RedisTemplate<String, String> redisTemplate) {
        this.localCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(Duration.ofMinutes(10))
                .build();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<String> get(String key) {
        String v = localCache.getIfPresent(key);
        if (v != null) return Optional.of(v);
        v = redisTemplate.opsForValue().get(key);
        if (v != null) localCache.put(key, v);
        return Optional.ofNullable(v);
    }

    @Override
    public void put(String key, String value, long ttlSeconds) {
        localCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public void invalidate(String key) {
        localCache.invalidate(key);
        redisTemplate.delete(key);
    }

    @Override
    public void invalidateByPrefix(String prefix) {
        // caution: keys() uses SCAN on Redis; okay for admin calls but avoid in hot paths
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        localCache.invalidateAll(); // coarse but safe
    }
}
