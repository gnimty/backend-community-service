package com.gnimty.communityapiserver.domain.riotaccount.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "riot_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RiotAccount extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "riot_account_id", columnDefinition = "BIGINT", updatable = false, unique = true)
	private Long id;

	@NotNull
	@Column(name = "internal_name", columnDefinition = "VARCHAR(100)")
	private String internalName;

	@NotNull
	@Column(name = "name", columnDefinition = "VARCHAR(100)")
	private String name;

	@NotNull
	@Column(name = "internal_tag_name", columnDefinition = "VARCHAR(100)", unique = true)
	private String internalTagName;

	@NotNull
	@Column(name = "tag_line", columnDefinition = "VARCHAR(100)")
	private String tagLine;

	@NotNull
	@Column(name = "is_main", columnDefinition = "TINYINT")
	private Boolean isMain;

	@NotNull
	@Column(name = "queue", columnDefinition = "VARCHAR(20)")
	@Enumerated(EnumType.STRING)
	private Tier queue;

	@NotNull
	@Column(name = "lp", columnDefinition = "BIGINT")
	private Long lp;

	@NotNull
	@Column(name = "division", columnDefinition = "TINYINT")
	private Integer division;

	@NotNull
	@Column(name = "mmr", columnDefinition = "BIGINT")
	private Long mmr;

	@Enumerated(EnumType.STRING)
	@Column(name = "frequent_lane_1", columnDefinition = "VARCHAR(10)")
	private Lane frequentLane1;

	@Enumerated(EnumType.STRING)
	@Column(name = "frequent_lane_2", columnDefinition = "VARCHAR(10)")
	private Lane frequentLane2;

	@Column(name = "frequent_champion_id_1", columnDefinition = "BIGINT")
	private Long frequentChampionId1;

	@Column(name = "frequent_champion_id_2", columnDefinition = "BIGINT")
	private Long frequentChampionId2;

	@Column(name = "frequent_champion_id_3", columnDefinition = "BIGINT")
	private Long frequentChampionId3;

	@NotNull
	@Column(name = "puuid", columnDefinition = "VARCHAR(100)")
	private String puuid;

	@NotNull
	@Column(name = "icon_id", columnDefinition = "BIGINT")
	private Long iconId;

	@NotNull
	@Column(name = "queue_flex", columnDefinition = "VARCHAR(20)")
	@Enumerated(EnumType.STRING)
	private Tier queueFlex;

	@NotNull
	@Column(name = "lp_flex", columnDefinition = "BIGINT")
	private Long lpFlex;

	@NotNull
	@Column(name = "division_flex", columnDefinition = "TINYINT")
	private Integer divisionFlex;

	@NotNull
	@Column(name = "mmr_flex", columnDefinition = "BIGINT")
	private Long mmrFlex;

	@Enumerated(EnumType.STRING)
	@Column(name = "frequent_lane_1_flex", columnDefinition = "VARCHAR(10)")
	private Lane frequentLane1Flex;

	@Enumerated(EnumType.STRING)
	@Column(name = "frequent_lane_2_flex", columnDefinition = "VARCHAR(10)")
	private Lane frequentLane2Flex;

	@Column(name = "frequent_champion_id_1_flex", columnDefinition = "BIGINT")
	private Long frequentChampionId1Flex;

	@Column(name = "frequent_champion_id_2_flex", columnDefinition = "BIGINT")
	private Long frequentChampionId2Flex;

	@Column(name = "frequent_champion_id_3_flex", columnDefinition = "BIGINT")
	private Long frequentChampionId3Flex;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public RiotAccount(
		String internalName,
		String name,
		String internalTagName,
		String tagLine,
		Boolean isMain,
		Tier queue,
		Long lp,
		Integer division,
		Long mmr,
		Lane frequentLane1,
		Lane frequentLane2,
		Long frequentChampionId1,
		Long frequentChampionId2,
		Long frequentChampionId3,
		String puuid,
		Long iconId,
		Tier queueFlex,
		Long lpFlex,
		Integer divisionFlex,
		Long mmrFlex,
		Lane frequentLane1Flex,
		Lane frequentLane2Flex,
		Long frequentChampionId1Flex,
		Long frequentChampionId2Flex,
		Long frequentChampionId3Flex,
		Member member
	) {
		this.internalName = internalName;
		this.name = name;
		this.internalTagName = internalTagName;
		this.tagLine = tagLine;
		this.isMain = isMain;
		this.queue = queue;
		this.lp = lp;
		this.division = division;
		this.mmr = mmr;
		this.frequentLane1 = frequentLane1;
		this.frequentLane2 = frequentLane2;
		this.frequentChampionId1 = frequentChampionId1;
		this.frequentChampionId2 = frequentChampionId2;
		this.frequentChampionId3 = frequentChampionId3;
		this.puuid = puuid;
		this.iconId = iconId;
		this.queueFlex = queueFlex;
		this.lpFlex = lpFlex;
		this.divisionFlex = divisionFlex;
		this.mmrFlex = mmrFlex;
		this.frequentLane1Flex = frequentLane1Flex;
		this.frequentLane2Flex = frequentLane2Flex;
		this.frequentChampionId1Flex = frequentChampionId1Flex;
		this.frequentChampionId2Flex = frequentChampionId2Flex;
		this.frequentChampionId3Flex = frequentChampionId3Flex;
		this.member = member;
	}

	public void updateIsMain() {
		isMain = !isMain;
	}
}
