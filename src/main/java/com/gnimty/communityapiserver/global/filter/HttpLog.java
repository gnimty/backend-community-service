package com.gnimty.communityapiserver.global.filter;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Getter
public class HttpLog {

	private final String method;
	private final String host;
	private final String requestUri;
	private final String requestParam;
	private final String requestHeaders;
	private final String requestBody;
	private final HttpStatus httpStatus;
	private final String responseHeaders;
	private final String responseBody;
	private final double elapsedTime;

	@Builder
	public HttpLog(
		ContentCachingRequestWrapper requestWrapper,
		ContentCachingResponseWrapper responseWrapper,
		double elapsedTime
	) throws UnsupportedEncodingException {
		this.method = requestWrapper.getMethod();
		this.host = requestWrapper.getHeader("x-real-ip");
		this.requestUri = requestWrapper.getRequestURI();
		this.requestParam = requestWrapper.getParameterMap().entrySet().stream()
			.map(entry -> entry.getKey() + "=[" + String.join(",", entry.getValue()) + "]")
			.collect(Collectors.joining(", "));
		this.requestHeaders = Collections.list(requestWrapper.getHeaderNames()).stream()
			.map(headerName -> headerName + ": " + requestWrapper.getHeader(headerName))
			.collect(Collectors.joining(", "));
		this.requestBody = new String(requestWrapper.getContentAsByteArray(), requestWrapper.getCharacterEncoding());
		this.httpStatus = HttpStatus.valueOf(responseWrapper.getStatus());
		this.responseHeaders = responseWrapper.getHeaderNames().stream()
			.distinct()
			.map(headerName -> headerName + ": " + responseWrapper.getHeaders(headerName))
			.collect(Collectors.joining(", "));
		this.responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());
		this.elapsedTime = elapsedTime;
	}

	@Override
	public String toString() {
		return String.format("""

				request:
				    HTTP Method  = %-20s
				    Host         = %-20s
				    Request URI  = %-20s
				    Parameters   = %s
				    Headers      = %s
				    Body         = %s

				response:
				    Status       = %-20d
				    Headers      = %s
				    Body         = %s

				elapsedTime: %f
				""",
			this.method,
			this.host,
			this.requestUri,
			this.requestParam,
			this.requestHeaders,
			this.requestBody,
			this.httpStatus.value(),
			this.responseHeaders,
			this.responseBody,
			this.elapsedTime
		);
	}
}
