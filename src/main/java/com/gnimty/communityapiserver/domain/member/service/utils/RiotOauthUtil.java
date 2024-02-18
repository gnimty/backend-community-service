package com.gnimty.communityapiserver.domain.member.service.utils;


import com.gnimty.communityapiserver.global.auth.JwtProvider;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class RiotOauthUtil {

    @Value("${oauth.riot.client_id}")
    private String client_id;
    @Value("${oauth.riot.client_secret}")
    private String client_secret;
    private final JwtProvider jwtProvider;

    public RiotAccountInfo getPuuid(String authCode, String redirectUri) {
        TokenInfo token;
        try {
            token = getTokenInfo(authCode, redirectUri);
        } catch (WebClientResponseException e) {
            throw new BaseException(ErrorCode.INVALID_AUTH_CODE);
        }
        return getAccountInfo(token);
    }

    private TokenInfo getTokenInfo(String authCode, String redirectUri) {
        return WebClient.create("https://auth.riotgames.com")
            .post()
            .uri("/token")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .headers(headers -> headers.setBasicAuth(client_id, client_secret))
            .bodyValue("grant_type=authorization_code&code=" + authCode + "&redirect_uri=" + redirectUri)
            .retrieve()
            .bodyToMono(TokenInfo.class)
            .block();
    }

    private RiotAccountInfo getAccountInfo(TokenInfo token) {
        return WebClient.create("https://asia.api.riotgames.com/riot/account/v1/accounts/me")
            .get()
            .headers(headers -> headers.setBearerAuth(token.getAccess_token()))
            .retrieve()
            .bodyToMono(RiotAccountInfo.class)
            .block();
    }

    @Getter
    public static class TokenInfo {

        private String access_token;
    }

    @Getter
    public static class RiotAccountInfo {

        private String gameName;
        private String tagLine;
        private String puuid;
    }

}
