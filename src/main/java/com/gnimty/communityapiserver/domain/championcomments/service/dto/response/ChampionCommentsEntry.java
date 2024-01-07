package com.gnimty.communityapiserver.domain.championcomments.service.dto.response;

import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChampionCommentsEntry {

	@Schema(example = "1", description = "챔피언 운용법 id")
	private Long id;
	@Schema(example = "TOP", description = "대상 라인")
	private Lane lane;
	@Schema(example = "1", description = "상대 챔피언 id")
	private Long opponentChampionId;
	@Schema(example = "0", description = "댓글 깊이")
	private Integer depth;
	@Schema(example = "1", description = "언급된 회원 id")
	private Long mentionedMemberId;
	@Schema(example = "content", description = "내용")
	private String contents;
	@Schema(example = "TIP", description = "댓글 타입")
	private CommentsType commentsType;
	@Schema(example = "10", description = "up count")
	private Long upCount;
	@Schema(example = "10", description = "down count")
	private Long downCount;
	@Schema(example = "1.1", description = "댓글이 작성된 당시 버전")
	private String version;
	@Schema(example = "2012-12-12T11:11:11", description = "댓글 작성 날짜")
	private LocalDateTime createdAt;
	@Schema(example = "2012-12-12T11:11:11", description = "댓글 수정 날짜")
	private LocalDateTime updatedAt;
	@Schema(example = "true", description = "삭제 여부")
	private Boolean deleted;
	@Schema(example = "true", description = "차단 여부")
	private Boolean blocked;
	@Schema(example = "1", description = "댓글 작성한 회원 id")
	private Long memberId;
	@Schema(example = "name", description = "댓글 작성한 회원 name")
	private String name;
	@Schema(example = "tag", description = "댓글 작성한 회원 tag")
	private String tagLine;
	@Schema(example = "true", description = "좋아요 여부")
	private Boolean like;
}
