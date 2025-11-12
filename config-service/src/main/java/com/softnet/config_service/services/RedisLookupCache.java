package com.softnet.config_service.services;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.softnet.config_service.cache.LookupCache;

@Service
public class RedisLookupCache implements LookupCache {

    private final RedisTemplate<String, String> redisTemplate;
    private final Cache<String, String> localCache;

    public RedisLookupCache(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.localCache = Caffeine.newBuilder().maximumSize(10000).expireAfterWrite(Duration.ofMinutes(10)).build();
    }

    private String mkKey(String code, String key, String tenant) {
        return String.format("lookup:%s:%s:%s", code, tenant == null ? "_global" : tenant, key == null ? "_all" : key);
    }

    @Override
    public Optional<String> get(String code, String key, String tenantId) {
        String sk = mkKey(code, key, tenantId);
        String v = localCache.getIfPresent(sk);
        if (v != null) {
            return Optional.of(v);
        }
        v = redisTemplate.opsForValue().get(sk);
        if (v != null) {
            localCache.put(sk, v);
            return Optional.of(v);
        }
        return Optional.empty();
    }

    @Override
    public void put(String code, String key, String tenantId, String jsonValue) {
        String sk = mkKey(code, key, tenantId);
        redisTemplate.opsForValue().set(sk, jsonValue, Duration.ofHours(1));
        localCache.put(sk, jsonValue);
    }

    @Override
    public void invalidate(String code, String tenantId) {
        // For simplicity: delete known pattern keys from Redis (needs redis keys scan in production)
        String keyPattern = String.format("lookup:%s:%s:*", code, tenantId == null ? "_global" : tenantId);
        // Use RedisScan or maintain list of keys via set; here we can call redis template keys (not recommended in prod)
        Set<String> keys = redisTemplate.keys(keyPattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        // clear local cache entries matching pattern (simple strategy: clearAll)
        localCache.invalidateAll();
    }

}
