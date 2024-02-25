package com.gnimty.communityapiserver.global.connect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketSessionManager {

	private static Map<String, SessionInfo> sessionStore = new ConcurrentHashMap<>();

	private static final long SESSION_TIMEOUT = 60 * 60 * 1000;
	private static final long ZOMBIE_CHECK_TIME = 24 * 60 * 60 * 1000;

	public void addSession(String sessionId, Long memberId) {
		long sessionExpireTime = System.currentTimeMillis() + SESSION_TIMEOUT;
		sessionStore.put(sessionId, new SessionInfo(memberId, sessionExpireTime));
	}

	public void deleteSession(String sessionId) {
		sessionStore.remove(sessionId);
	}

	public Long getMemberId(String sessionId) {
		return sessionStore.get(sessionId).getMemberId();
	}

	public int getSessionCountByMemberId(Long memberId) {
		return (int) sessionStore.values().stream()
			.filter(memberId::equals)
			.count();
	}

	@Scheduled(fixedRate = SESSION_TIMEOUT)
	private void cleanExpiredSessions() {
		long currentTime = System.currentTimeMillis();
		sessionStore.entrySet().removeIf(session -> session.getValue().getExpirationTime() < currentTime);
	}

	@Scheduled(fixedRate = ZOMBIE_CHECK_TIME)
	private void log() {
		long currentTime = System.currentTimeMillis();

		List<SessionInfo> zombieSessions = sessionStore.values().stream()
			.filter(sessionInfo -> sessionInfo.getExpirationTime() < currentTime)
			.toList();

		log.info("""
          
        [ZombieSession]
            count: {}
            Sessions:
            				{}
        """
			, zombieSessions.size(), zombieSessions.stream()
				.map(Object::toString)
				.collect(Collectors.joining("\n\t\t\t\t\t\t")));

	}
}