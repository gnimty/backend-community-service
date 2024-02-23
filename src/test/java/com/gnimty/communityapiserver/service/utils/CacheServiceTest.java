package com.gnimty.communityapiserver.service.utils;

import static com.gnimty.communityapiserver.global.constant.CacheType.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gnimty.communityapiserver.global.utils.CacheService;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import com.google.common.testing.FakeTicker;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

public class CacheServiceTest extends ServiceTestSupport {

	@Autowired
	private CacheService cacheService;
	@Autowired
	private CacheManager cacheManager;

	@AfterEach
	void tearDown() {
		cacheManager.getCacheNames()
			.forEach(cacheName -> Objects.requireNonNull(cacheManager.getCache(cacheName)).clear());
	}

	@Nested
	@DisplayName("put 시")
	class Put {

		@DisplayName("최초 put 시 expired time에 맞는 데이터가 입력된다.")
		@Test
		void should_putKeyValueWithExpiredTime_when_keyNotExists() {
			cacheService.put(REFRESH_TOKEN, "key", "value");
			assertThat(cacheService.get(REFRESH_TOKEN, "key")).isEqualTo("value");
		}

		@DisplayName("원래 존재하던 key에 put을 하면 갱신되며, expired time 또한 늘어난다.")
		@Test
		void should_updateValueAndExpiredTime_when_keyExists() {
			FakeTicker ticker = new FakeTicker();
			Cache<String, String> cache = Caffeine.newBuilder()
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.executor(Runnable::run)
				.ticker(ticker::read)
				.maximumSize(10)
				.build();

			cache.put("key", "value");
			ticker.advance(9, TimeUnit.MINUTES);
			assertThat(cache.getIfPresent("key")).isEqualTo("value");
			cache.put("key", "value2");
			ticker.advance(9, TimeUnit.MINUTES);
			assertThat(cache.getIfPresent("key")).isEqualTo("value2");
		}

		@DisplayName("expiredTime이 지난 cache는 삭제된다.")
		@Test
		void should_remove_when_pastExpiredTime() {
			FakeTicker ticker = new FakeTicker();
			Cache<String, String> cache = Caffeine.newBuilder()
				.expireAfterWrite(10, TimeUnit.MINUTES)
				.executor(Runnable::run)
				.ticker(ticker::read)
				.maximumSize(10)
				.build();

			cache.put("key", "value");
			ticker.advance(30, TimeUnit.MINUTES);
			assertThat(cacheService.get(REFRESH_TOKEN, "key")).isNull();
		}
	}

	@Nested
	@DisplayName("evict 시")
	class Evict {

		@DisplayName("원래 존재하던 key라면, 삭제되어야 한다.")
		@Test
		void should_delete_when_alreadyExistsKey() {
			cacheService.put(REFRESH_TOKEN, "key", "token");
			cacheService.evict(REFRESH_TOKEN, "key");
			assertThat(cacheService.get(REFRESH_TOKEN, "key")).isNull();
		}

		@DisplayName("존재하지 않는 key라도, 예외를 발생시키지 않는다.")
		@Test
		void should_noException_when_notExistsKey() {
			assertThatNoException().isThrownBy(() -> cacheService.evict(REFRESH_TOKEN, "any"));
		}
	}

	@Nested
	@DisplayName("get 시")
	class Get {

		@DisplayName("key에 해당하는 캐시 데이터가 존재하면, 반환한다.")
		@Test
		void should_returnValue_when_dataExistsRelevantKey() {
			cacheService.put(REFRESH_TOKEN, "key", "token");
			assertThat(cacheService.get(REFRESH_TOKEN, "key")).isEqualTo("token");
		}

		@DisplayName("key에 해당하는 캐시 데이터가 존재하지 않으면, null을 반환한다.")
		@Test
		void should_returnNull_when_dataNotExistsRelevantKey() {
			assertThat(cacheService.get(REFRESH_TOKEN, "key")).isNull();
		}
	}
}
