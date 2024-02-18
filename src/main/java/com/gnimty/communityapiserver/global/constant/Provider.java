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
		KAKAO - 카카오
		GOOGLE - 구글
		"""
)
@Getter
public enum Provider {

	KAKAO,
	GOOGLE;

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static Provider findByInput(String input) {
		return Stream.of(Provider.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
