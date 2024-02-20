package com.gnimty.communityapiserver.global.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.gnimty.communityapiserver.global.constant.CacheType;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

	@Bean
	public CacheManager cacheManager() {
		List<CaffeineCache> caffeineCaches = Arrays.stream(CacheType.values())
			.map(cacheType -> new CaffeineCache(cacheType.getCacheName(), Caffeine.newBuilder()
				.recordStats() // cache profiling
				.expireAfterWrite(cacheType.getExpiredTime(), TimeUnit.MILLISECONDS)
				.maximumSize(cacheType.getMaximumSize())
				.build()))
			.toList();

		SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
		simpleCacheManager.setCaches(caffeineCaches);

		return simpleCacheManager;
	}
}
