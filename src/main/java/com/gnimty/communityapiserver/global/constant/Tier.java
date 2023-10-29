package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Tier {

	CHALLENGER("challenger", 9),
	GRANDMASTER("grandmaster", 8),
	MASTER("master", 7),
	DIAMOND("diamond", 6),
	EMERALD("emerald", 5),
	PLATINUM("platinum", 4),
	GOLD("gold", 3),
	SILVER("silver", 2),
	BRONZE("bronze", 1),
	IRON("iron", 0),
	NULL("null", -1);

	private final String tier;
	private final Integer weight;

	Tier(String tier, Integer weight) {
		this.tier = tier;
		this.weight = weight;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static Tier findByInput(String input) {
		return Stream.of(Tier.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
