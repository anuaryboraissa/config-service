package com.softnet.lookups_service.cache;

import java.util.Optional;

public interface CacheService {
    Optional<String> get(String key);
    void put(String key, String value, long ttlSeconds);
    void invalidate(String key);
    void invalidateByPrefix(String prefix);
}
