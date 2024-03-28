package com.gnimty.communityapiserver.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiDocConfiguration {

	private final List<Server> servers;
	private final Server localServer;
	private final Server productionServer;
	private final String baseUrl;

	public ApiDocConfiguration(@Value("${gnimty.base-url}") String baseUrl) {
		this.baseUrl = baseUrl;
		this.localServer = new Server();
		this.productionServer = new Server();

		setServerUrls();
		servers = List.of(localServer, productionServer);
	}

	public void setServerUrls() {
		localServer.setUrl("http://localhost:8080/community");
		productionServer.setUrl(baseUrl + "/community");
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

