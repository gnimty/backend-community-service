package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SummonerUpdateEntry {

	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String internalName;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String name;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String internalTagName;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String tagLine;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private String puuid;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Tier tier;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer division;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long lp;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long mmr;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private List<Lane> mostLanes;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private List<Long> mostChampionIds;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long iconId;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Tier tierFlex;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long lpFlex;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Integer divisionFlex;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private Long mmrFlex;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private List<Lane> mostLanesFlex;
	@NotNull(message = ErrorMessage.INVALID_INPUT_VALUE)
	private List<Long> mostChampionIdsFlex;
}
