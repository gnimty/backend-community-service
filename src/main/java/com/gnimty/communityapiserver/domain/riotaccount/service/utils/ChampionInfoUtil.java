package com.gnimty.communityapiserver.domain.riotaccount.service.utils;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.CHAMPION_ID_INVALID;

import com.gnimty.communityapiserver.global.exception.BaseException;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ChampionInfoUtil {

	public static Set<Long> CHAMPION_IDS = new HashSet<>();

	public static void validateChampionId(Long championId) {
		if (championId == null) {
			return;
		}
		if (!CHAMPION_IDS.contains(championId)) {
			throw new BaseException(CHAMPION_ID_INVALID);
		}
	}
}
