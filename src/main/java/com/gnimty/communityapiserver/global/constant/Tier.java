package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.stream.Stream;

public enum Tier {

	CHALLENGER("challenger"),
	GRANDMASTER("grandmaster"),
	MASTER("master"),
	DIAMOND("diamond"),
	EMERALD("emerald"),
	PLATINUM("platinum"),
	GOLD("gold"),
	SILVER("silver"),
	BRONZE("bronze"),
	IRON("iron"),
	NULL("null");

	private final String tier;

	Tier(String tier) {
		this.tier = tier;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static Tier findByInput(String input) {
		return Stream.of(Tier.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
