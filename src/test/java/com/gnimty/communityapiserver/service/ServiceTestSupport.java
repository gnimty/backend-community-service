package com.gnimty.communityapiserver.service;

import com.gnimty.communityapiserver.config.RedisTestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ExtendWith(RedisTestConfig.class)
public abstract class ServiceTestSupport {

    @LocalServerPort
    public int port;
}
