package com.gnimty.communityapiserver.global.auth;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebSocketSessionManager {

    private static Map<String, User> sessionStore = new ConcurrentHashMap<>();

    public void addSession(String sessionId, User user) {
        sessionStore.put(sessionId, user);
    }

    public User disConnectSession(String sessionId) {
        deleteSession(sessionId);
        return getUser(sessionId);
    }

    public User getUser(String sessionId)  {
        return sessionStore.get(sessionId);
    }

    private void deleteSession(String sessionId) {
        sessionStore.remove(sessionId);
    }
}