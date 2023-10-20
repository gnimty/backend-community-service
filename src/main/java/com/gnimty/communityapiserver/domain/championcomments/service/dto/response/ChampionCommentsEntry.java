package com.gnimty.communityapiserver.domain.championcomments.service.dto.response;

import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ChampionCommentsEntry {

	private Long id;
	private Lane lane;
	private Long opponentChampionId;
	private Integer depth;
	private Long mentionedMemberId;
	private String contents;
	private CommentsType commentsType;
	private Long upCount;
	private Long downCount;
	private String version;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private Boolean deleted;
	private Boolean blocked;
	private Long memberId;
	private String summonerName;
}
