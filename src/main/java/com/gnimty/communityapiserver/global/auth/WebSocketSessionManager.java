package com.gnimty.communityapiserver.global.auth;

import com.gnimty.communityapiserver.domain.member.service.MemberService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionManager {

    private static Map<String, Long> sessionStore = new ConcurrentHashMap<>();

    public void addSession(String sessionId, Long memberId) {
        sessionStore.put(sessionId, memberId);
    }

    public Long removeSession(String sessionId) {
        Long memberId = getMemberId(sessionId);
        sessionStore.remove(sessionId);
        return memberId;
    }

    public Long getMemberId(String sessionId)  {
        return sessionStore.get(sessionId);
    }

}