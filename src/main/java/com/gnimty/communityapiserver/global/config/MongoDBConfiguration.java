package com.gnimty.communityapiserver.global.config;


import java.io.IOException;
import java.util.Arrays;

import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
public class MongoDBConfiguration {

	@Bean
	public MongoCustomConversions mongoCustomConversions() {

		return new MongoCustomConversions(
			Arrays.asList());
	}

//	@Bean
//	public MongoTemplate mongoTemplate() throws IOException {
//
//		EmbeddedMongoProperties mongo = new EmbeddedMongoFactoryBean();
//		mongo.setBindIp("localhost");
//		MongoClient mongoClient = mongo.getObject();
//		MongoTemplate mongoTemplate = new MongoTemplate(mongoClient, "test_or_whatever_you_want_to_call_this_db");
//		return mongoTemplate;
//	}

}
