package com.gnimty.communityapiserver.domain.championcomments.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.CommentsType;
import com.gnimty.communityapiserver.global.constant.Lane;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "champion_comments",
	indexes = {
		@Index(name = "champion_comments_select_idx", columnList = "champion_id, depth, up_count DESC, down_count, created_at")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChampionComments extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "champion_comments_id", columnDefinition = "BIGINT", updatable = false, unique = true)
	private Long id;

	@Column(name = "lane", columnDefinition = "VARCHAR(10)")
	@Enumerated(EnumType.STRING)
	private Lane lane;

	@NotNull
	@Column(name = "champion_id", columnDefinition = "BIGINT")
	private Long championId;

	@Column(name = "opponent_champion_id", columnDefinition = "BIGINT")
	private Long opponentChampionId;

	@NotNull
	@Column(name = "depth", columnDefinition = "TINYINT")
	private Integer depth;

	@Column(name = "mentioned_member_id", columnDefinition = "BIGINT")
	private Long mentionedMemberId;

	@NotNull
	@Column(name = "contents", columnDefinition = "VARCHAR(1000)")
	private String contents;

	@Column(name = "comments_type", columnDefinition = "VARCHAR(20)")
	@Enumerated(EnumType.STRING)
	private CommentsType commentsType;

	@NotNull
	@Column(name = "up_count", columnDefinition = "BIGINT default 0")
	private Long upCount;

	@NotNull
	@Column(name = "down_count", columnDefinition = "BIGINT default 0")
	private Long downCount;

	@NotNull
	@Column(name = "version", columnDefinition = "VARCHAR(20)")
	private String version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_champion_comments_id")
	private ChampionComments parentChampionComments;

	@Version
	private Long lockVersion;

	@Builder
	public ChampionComments(
		Lane lane,
		Long championId,
		Long opponentChampionId,
		Integer depth,
		Long mentionedMemberId,
		String contents,
		CommentsType commentsType,
		Long upCount,
		Long downCount,
		String version,
		Member member,
		ChampionComments parentChampionComments
	) {
		this.lane = lane;
		this.championId = championId;
		this.opponentChampionId = opponentChampionId;
		this.depth = depth;
		this.mentionedMemberId = mentionedMemberId;
		this.contents = contents;
		this.commentsType = commentsType;
		this.upCount = upCount;
		this.downCount = downCount;
		this.version = version;
		this.member = member;
		this.parentChampionComments = parentChampionComments;
	}

	public void updateMentionedMemberId(Long mentionedMemberId) {
		if (mentionedMemberId == null) {
			return;
		}
		this.mentionedMemberId = mentionedMemberId;
	}

	public void updateContents(String contents) {
		this.contents = contents;
	}

	public void increaseUpCount() {
		this.upCount++;
	}

	public void increaseDownCount() {
		this.downCount++;
	}

	@Override
	public void delete() {
		super.delete();
		member = null;
	}
}
