package com.gnimty.communityapiserver.global.connect;

import java.time.LocalDateTime;
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

	private static final Map<String, SessionInfo> sessionStore = new ConcurrentHashMap<>();

	private static final long SESSION_TIMEOUT = 60 * 60 * 1000;
	private static final long ZOMBIE_CHECK_TIME = 24 * 60 * 60 * 1000;

	public void addSession(String sessionId, Long memberId) {
		sessionStore.put(sessionId, new SessionInfo(memberId, LocalDateTime.now().plusHours(1)));
	}

	public void deleteSession(String sessionId) {
		sessionStore.remove(sessionId);
	}

	public Long getMemberId(String sessionId) {
		SessionInfo sessionInfo = sessionStore.get(sessionId);
		if (sessionInfo == null) {
			return -1L;
		}
		return sessionInfo.getMemberId();
	}

	public int getSessionCountByMemberId(Long memberId) {
		return (int) sessionStore.values().stream()
			.filter(value -> value.getMemberId().equals(memberId))
			.count();
	}

	@Scheduled(fixedRate = SESSION_TIMEOUT)
	private void cleanExpiredSessions() {
		sessionStore.values().removeIf(session -> isBeforeNow(session.getExpirationTime()));
	}

	@Scheduled(fixedRate = ZOMBIE_CHECK_TIME)
	private void log() {
		List<SessionInfo> zombieSessions = sessionStore.values().stream()
			.filter(sessionInfo -> isBeforeNow(sessionInfo.getExpirationTime()))
			.toList();

		log.info("""
				  
				[ZombieSessions]
				    count: {}
				    Sessions:
				    				{}
				"""
			, zombieSessions.size(), zombieSessions.stream()
				.map(Object::toString)
				.collect(Collectors.joining("\n\t\t\t\t\t\t")));
	}

	public boolean isBeforeNow(LocalDateTime time) {
		return time.isAfter(LocalDateTime.now());
	}
}