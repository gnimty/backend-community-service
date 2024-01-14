package com.gnimty.communityapiserver.global.config;


import com.gnimty.communityapiserver.global.constant.converter.StringToEnumConverterFactory;
import com.gnimty.communityapiserver.global.interceptor.TokenAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfiguration implements WebMvcConfigurer {

	private final TokenAuthInterceptor tokenAuthInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("https://gnimty.kro.kr")
			.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
			.allowCredentials(true)
			.allowedHeaders("*");
	}

	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEnumConverterFactory());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
			.addInterceptor(tokenAuthInterceptor)
			.addPathPatterns("/**")
			.excludePathPatterns("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**")
			.excludePathPatterns("/auth/**", "/oauth/**")
			.excludePathPatterns("/members/password/**")
			.excludePathPatterns(
				"/css/**", "/*.ico"
				, "/error", "/error-page/**"
			);
	}
}
