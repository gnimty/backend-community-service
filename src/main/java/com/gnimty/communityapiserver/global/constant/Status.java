package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;
import lombok.Getter;

@Schema(
	enumAsRef = true,
	description = """
		ONLINE - 온라인
		OFFLINE - 오프라인
		AWAY - 자리비움
		""")
@Getter
public enum Status {

	ONLINE("온라인"),
	OFFLINE("오프라인"),
	AWAY("자리비움");

	private final String status;

	Status(String status) {
		this.status = status;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static Status findByInput(String input) {
		return Stream.of(Status.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
