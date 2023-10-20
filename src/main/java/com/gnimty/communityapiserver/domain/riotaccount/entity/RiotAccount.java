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
	@Column(name = "summoner_name", columnDefinition = "VARCHAR(100)", unique = true)
	private String summonerName;

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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Builder
	public RiotAccount(
		String summonerName,
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
		Member member
	) {
		this.summonerName = summonerName;
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
		this.member = member;
	}

	public void updateIsMain() {
		isMain = !isMain;
	}
}
