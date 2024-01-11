package com.gnimty.communityapiserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocConfiguration {

	private final List<Server> servers;
	private final Server server;

	public ApiDocConfiguration() {
		this.server = new Server();

		setServerUrls();
		servers = List.of(server);
	}

	public void setServerUrls() {
		server.setUrl("https://gnimty.kro.kr");
	}

	@Bean
	public OpenAPI gnimtyOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("GNIMTY open API")
				.description("GNIMTY open API")
				.version("v1.0"))
			.servers(servers);
	}
}

