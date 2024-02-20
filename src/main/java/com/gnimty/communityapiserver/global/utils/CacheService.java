package com.gnimty.communityapiserver.global.utils;

import com.gnimty.communityapiserver.global.constant.CacheType;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CacheService {

	private final CacheManager cacheManager;

	public void put(CacheType cacheType, String key, String value) {
		Cache cache = cacheManager.getCache(cacheType.getCacheName());
		assert cache != null;
		cache.put(key, value);
	}

	public String get(CacheType cacheType, String key) {
		Cache cache = cacheManager.getCache(cacheType.getCacheName());
		assert cache != null;
		return cache.get(key, String.class);
	}

	public void evict(CacheType cacheType, String key) {
		Cache cache = cacheManager.getCache(cacheType.getCacheName());
		assert cache != null;
		cache.evict(key);
	}

	public static String getCacheKey(KeyPrefix prefix, String key) {
		return prefix.getPrefix() + key;
	}
}
