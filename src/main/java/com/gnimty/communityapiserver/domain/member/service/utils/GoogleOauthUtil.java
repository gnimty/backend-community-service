package com.gnimty.communityapiserver.domain.member.service.utils;

import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class GoogleOauthUtil {

    @Value("${oauth.google.client_id}")
    private String client_id;
    @Value("${oauth.google.client_secret}")
    private String client_secret;
    @Value("${oauth.google.redirect_uri}")
    private String redirect_uri;
    private final JwtProvider jwtProvider;

    public String getGoogleUserEmail(String authCode) {
        TokenInfo block;
        try {
            block = getTokenInfo(authCode);
        } catch (WebClientResponseException e) {
            throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
        }
        return jwtProvider.getEmailByToken(block.getId_token());
    }

    private TokenInfo getTokenInfo(String authCode) {
        MultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("grant_type", "authorization_code");
        bodyMap.add("client_id", client_id);
        bodyMap.add("client_secret", client_secret);
        bodyMap.add("redirect_uri", redirect_uri);
        bodyMap.add("code", authCode);

        return WebClient.create("https://oauth2.googleapis.com")
            .post()
            .uri("/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(bodyMap))
            .retrieve()
            .bodyToMono(TokenInfo.class)
            .block();
    }

    @Getter
    public static class TokenInfo {

        private String id_token;
    }
}
