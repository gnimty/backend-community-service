package com.gnimty.communityapiserver.global.config;


import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoDBConfiguration {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {

		return new MongoCustomConversions(
			Arrays.asList());
	}

}
