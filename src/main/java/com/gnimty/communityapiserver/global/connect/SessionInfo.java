package com.gnimty.communityapiserver.global.connect;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
public class SessionInfo {

	private Long memberId;
	private long expirationTime;

	@Override
	public String toString() {
		String dateTime = new DateTime(this.expirationTime).toString();

		return String.format("""
                [memberId  = %d,\t\texpirationTime   = %s]
                """,
			memberId,
			dateTime
		);
	}
}
