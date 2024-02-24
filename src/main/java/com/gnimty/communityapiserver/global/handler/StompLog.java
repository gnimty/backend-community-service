package com.gnimty.communityapiserver.global.handler;

import lombok.Builder;

public class StompLog {
	private final String command;
	private final String destination;
	private final String sessionId;

	@Builder
	public StompLog(String command, String destination, String sessionId) {
		this.command = command;
		this.destination = destination;
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return String.format("""
				chat client:
				    Command         = %s
				    Destination     = %s
				    SessionId       = %s
				    """,
			this.command,
			this.destination,
			this.sessionId
		);
	}
}
