package com.gnimty.communityapiserver.global.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketSessionManager {

    private static Map<String, Long> sessionStore = new ConcurrentHashMap<>();

    public void addSession(String sessionId, Long memberId) {
        sessionStore.put(sessionId, memberId);
    }

    public Long disConnectSession(String sessionId) {
        Long memberId = getMemberId(sessionId);
        deleteSession(sessionId);
        return memberId;
    }

    public Long getMemberId(String sessionId)  {
        return sessionStore.get(sessionId);
    }

    private void deleteSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}