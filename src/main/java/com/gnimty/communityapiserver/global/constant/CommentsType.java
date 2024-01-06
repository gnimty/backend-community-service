package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.stream.Stream;

@Schema(
	enumAsRef = true,
	description = """
		QUESTION - 질문
		TIP - 팁
		"""
)
public enum CommentsType {

	QUESTION,
	TIP;

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static CommentsType findByInput(String input) {
		return Stream.of(CommentsType.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
