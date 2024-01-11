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
	private final Server localServer;
	private final Server productionServer;

	public ApiDocConfiguration() {
		this.localServer = new Server();
		this.productionServer = new Server();

		setServerUrls();
		servers = List.of(localServer, productionServer);
	}

	public void setServerUrls() {
		localServer.setUrl("http://localhost:8080/community");
		productionServer.setUrl("https://gnimty.kro.kr/community");
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

