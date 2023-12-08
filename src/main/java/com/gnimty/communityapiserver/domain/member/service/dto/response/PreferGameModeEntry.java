package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.global.constant.GameMode;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PreferGameModeEntry {

	@NotNull
	private GameMode gameMode;

	public static PreferGameModeEntry from(PreferGameMode preferGameMode) {
		return PreferGameModeEntry.builder()
			.gameMode(preferGameMode.getGameMode())
			.build();
	}
}
