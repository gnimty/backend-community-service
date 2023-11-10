package com.gnimty.communityapiserver.global.config;


import com.gnimty.communityapiserver.global.constant.converter.StringToEnumConverterFactory;
import com.gnimty.communityapiserver.global.interceptor.MemberAuthInterceptor;
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
	private final MemberAuthInterceptor memberAuthInterceptor;

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("http://localhost:3000", "https://localhost:3000")
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
			.excludePathPatterns("/auth/**")
			.excludePathPatterns("/oauth/**")
			.excludePathPatterns(
				"/css/**", "/*.ico"
				, "/error", "/error-page/**"
			);

		registry
			.addInterceptor(memberAuthInterceptor)
			.addPathPatterns("/members/**")
			.excludePathPatterns(
				"/css/**", "/*.ico"
				, "/error", "/error-page/**", "/members/me", "/members/password/**");
	}
}
