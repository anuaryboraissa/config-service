package com.softnet.config_service.cache;

import java.util.Optional;

public interface LookupCache {
  Optional<String> get(String code, String key, String tenantId);
  void put(String code, String key, String tenantId, String jsonValue);
  void invalidate(String code, String tenantId);
}
