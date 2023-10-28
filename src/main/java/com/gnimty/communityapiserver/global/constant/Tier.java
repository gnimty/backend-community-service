package com.gnimty.communityapiserver.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum Tier {

	CHALLENGER("challenger", 10),
	GRANDMASTER("grandmaster", 9),
	MASTER("master", 8),
	DIAMOND("diamond", 7),
	EMERALD("emerald", 6),
	PLATINUM("platinum", 5),
	GOLD("gold", 4),
	SILVER("silver", 3),
	BRONZE("bronze", 2),
	IRON("iron", 1),
	NULL("null", 0);

	private final String tier;
	private final Integer order;

	Tier(String tier, Integer order) {
		this.tier = tier;
		this.order = order;
	}

	@JsonCreator(mode = JsonCreator.Mode.DELEGATING)
	public static Tier findByInput(String input) {
		return Stream.of(Tier.values())
			.filter(c -> c.name().equals(input))
			.findFirst()
			.orElseThrow(() -> new BaseException(ErrorCode.INVALID_ENUM_VALUE));
	}
}
