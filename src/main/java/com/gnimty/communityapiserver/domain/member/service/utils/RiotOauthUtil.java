package com.gnimty.communityapiserver.domain.member.service.utils;


import static com.gnimty.communityapiserver.global.constant.WebClientType.AUTHORIZATION_CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GRANT_TYPE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.REDIRECT_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.RIOT_ACCOUNT_REQUEST_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.RIOT_TOKEN_REQUEST_URI;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.gnimty.communityapiserver.global.dto.webclient.RiotAccountInfo;
import com.gnimty.communityapiserver.global.dto.webclient.RiotTokenInfo;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class RiotOauthUtil {

	@Value("${oauth.riot.client_id}")
	private String client_id;
	@Value("${oauth.riot.client_secret}")
	private String client_secret;

	public RiotAccountInfo getPuuid(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = getTokenInfoBodyMap(authCode, redirectUri);
		RiotTokenInfo tokenInfo = WebClientUtil.post(RiotTokenInfo.class, RIOT_TOKEN_REQUEST_URI.getValue(),
			APPLICATION_FORM_URLENCODED, bodyMap, httpHeaders -> httpHeaders.setBasicAuth(client_id, client_secret));
		return WebClientUtil.get(RiotAccountInfo.class, RIOT_ACCOUNT_REQUEST_URI.getValue(),
			httpHeaders -> httpHeaders.setBearerAuth(tokenInfo.getAccess_token()));
	}

	private MultiValueMap<String, String> getTokenInfoBodyMap(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add(GRANT_TYPE.getValue(), AUTHORIZATION_CODE.getValue());
		bodyMap.add(CODE.getValue(), authCode);
		bodyMap.add(REDIRECT_URI.getValue(), redirectUri);
		return bodyMap;
	}
}
