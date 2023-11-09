package com.gnimty.communityapiserver.config;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestConfig implements BeforeAllCallback {

	private static final String REDIS_IMAGE = "redis:latest";
	private static final int REDIS_PORT = 6379;
	private GenericContainer redis;

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		redis = new GenericContainer(DockerImageName.parse(REDIS_IMAGE))
			.withExposedPorts(REDIS_PORT);
		redis.start();
		System.setProperty("spring.redis.host", redis.getHost());
		System.setProperty("spring.redis.port",
			String.valueOf(redis.getMappedPort(REDIS_PORT)));
	}
}
