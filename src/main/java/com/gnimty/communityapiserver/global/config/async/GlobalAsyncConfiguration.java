package com.gnimty.communityapiserver.global.config.async;

import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@Slf4j
@EnableAsync
public class GlobalAsyncConfiguration implements AsyncConfigurer {

	@Bean(name = "riotAccountExecutor")
	public Executor riotAccountExecutor() {
		return createExecutor("RiotAccountExecutor-");
	}

	@Bean(name = "mailExecutor")
	public Executor mailExecutor() {
		return createExecutor("MailExecutor-");
	}

	private Executor createExecutor(String threadNamePrefix) {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(5);
		executor.setQueueCapacity(10);
		executor.setThreadNamePrefix(threadNamePrefix);
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) ->
			log.error("Exception handler for async method '" + method.toGenericString()
				+ "' threw unexpected exception itself", ex);
	}
}
