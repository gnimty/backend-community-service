package com.gnimty.communityapiserver.global.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Order
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

	public static final String[] BLACK_LIST = {
		"swagger",
		"api-docs"
	};

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

		if (Arrays.stream(BLACK_LIST).anyMatch(blackList -> request.getRequestURI().contains(blackList))) {
			filterChain.doFilter(request, response);
			return;
		}

		long start = System.currentTimeMillis();
		filterChain.doFilter(requestWrapper, responseWrapper);
		long end = System.currentTimeMillis();

		try {
			log.info(
				HttpLog.builder()
					.requestWrapper(requestWrapper)
					.responseWrapper(responseWrapper)
					.elapsedTime((end - start) / 1000.0)
					.build()
					.toString()
			);

			responseWrapper.copyBodyToResponse();
		} catch (UnsupportedEncodingException e) {
			log.error("Logging failure: {}", e.getMessage());
		}
	}
}
