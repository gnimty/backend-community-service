package com.gnimty.communityapiserver.global.connect;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionInfo {

	private Long memberId;
	private LocalDateTime expirationTime;

	@Override
	public String toString() {
		return String.format("""
				[memberId  = %d,\t\texpirationTime   = %s]
				""",
			memberId,
			expirationTime.toString()
		);
	}
}
