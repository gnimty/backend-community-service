package com.gnimty.communityapiserver.global.auth;

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


    public void deleteSession(String sessionId) {
        sessionStore.remove(sessionId);
    }

    public Long getMemberId(String sessionId)  {
        return sessionStore.get(sessionId);
    }

    public int getSessionCountByMemberId(Long memberId) {
        return (int) sessionStore.values().stream()
            .filter(memberId::equals)
            .count();
    }

}