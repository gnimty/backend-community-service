package com.gnimty.communityapiserver;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableScheduling
public class CommunityapiserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(CommunityapiserverApplication.class, args);
	}

	@PostConstruct
	void setUp() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

}
