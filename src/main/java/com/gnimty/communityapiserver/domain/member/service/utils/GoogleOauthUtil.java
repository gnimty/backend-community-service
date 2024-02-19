package com.gnimty.communityapiserver.domain.member.service.utils;

import static com.gnimty.communityapiserver.global.constant.WebClientType.AUTHORIZATION_CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CLIENT_ID;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CLIENT_SECRET;
import static com.gnimty.communityapiserver.global.constant.WebClientType.CODE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GOOGLE_TOKEN_REQUEST_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GOOGLE_USER_INFO_REQUEST_URI;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GRANT_TYPE;
import static com.gnimty.communityapiserver.global.constant.WebClientType.REDIRECT_URI;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

import com.gnimty.communityapiserver.global.dto.webclient.GoogleTokenInfo;
import com.gnimty.communityapiserver.global.dto.webclient.GoogleUserInfo;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
@RequiredArgsConstructor
public class GoogleOauthUtil {

	@Value("${oauth.google.client_id}")
	private String client_id;
	@Value("${oauth.google.client_secret}")
	private String client_secret;

	public String getGoogleUserEmail(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = getTokenInfoBodyMap(authCode, redirectUri);
		GoogleTokenInfo tokenInfo = WebClientUtil.post(GoogleTokenInfo.class, GOOGLE_TOKEN_REQUEST_URI.getValue(),
			APPLICATION_FORM_URLENCODED, bodyMap, null);
		GoogleUserInfo userInfo = WebClientUtil.get(GoogleUserInfo.class,
			GOOGLE_USER_INFO_REQUEST_URI.getValue() + tokenInfo.getAccess_token(), null);
		return userInfo.getEmail();
	}

	private MultiValueMap<String, String> getTokenInfoBodyMap(String authCode, String redirectUri) {
		MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
		bodyMap.add(GRANT_TYPE.getValue(), AUTHORIZATION_CODE.getValue());
		bodyMap.add(CLIENT_ID.getValue(), client_id);
		bodyMap.add(CLIENT_SECRET.getValue(), client_secret);
		bodyMap.add(REDIRECT_URI.getValue(), redirectUri);
		bodyMap.add(CODE.getValue(), authCode);
		return bodyMap;
	}
}
