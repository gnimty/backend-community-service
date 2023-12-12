package com.gnimty.communityapiserver.domain.chat.entity;


import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class User {

	@Id
	private String id;
	@Indexed(unique = true)
	private Long actualUserId;

	private String name;
	private String tagLine;
	private String internalTagName;
	private Status status;
	private Long profileIconId;
	private String puuid;

	// 솔로 랭크
	private Tier tier;
	private Integer division;
	private Long lp;
	private Long mmr;
	private List<Lane> mostLanes;
	private List<Long> mostChampions;

	// 자유 랭크
	private Tier tierFlex;
	private Integer divisionFlex;
	private Long lpFlex;
	private Long mmrFlex;
	private List<Lane> mostLanesFlex;
	private List<Long> mostChampionsFlex;


	public static User toUser(RiotAccount riotAccount) {
		return User.builder()
			.actualUserId(riotAccount.getMember().getId())
			.name(riotAccount.getName())
			.tagLine(riotAccount.getTagLine())
			.internalTagName(riotAccount.getInternalTagName())
			.profileIconId(riotAccount.getIconId())
			.puuid(riotAccount.getPuuid())
			// 솔로 랭크
			.tier(riotAccount.getQueue())
			.division(riotAccount.getDivision())
			.lp(riotAccount.getLp())
			.mmr(riotAccount.getMmr())
			.mostLanes(toListMostItems(riotAccount.getFrequentLane1(), riotAccount.getFrequentLane2()))
			.mostChampions(
				toListMostItems(riotAccount.getFrequentChampionId1(), riotAccount.getFrequentChampionId2(),
					riotAccount.getFrequentChampionId3()))
			// 자유 랭크
			.tierFlex(riotAccount.getQueueFlex())
			.divisionFlex(riotAccount.getDivisionFlex())
			.lpFlex(riotAccount.getLpFlex())
			.mmrFlex(riotAccount.getMmrFlex())
			.mostLanesFlex(toListMostItems(riotAccount.getFrequentLane1Flex(), riotAccount.getFrequentLane2Flex()))
			.mostChampionsFlex(
				toListMostItems(riotAccount.getFrequentChampionId1Flex(), riotAccount.getFrequentChampionId2Flex(),
					riotAccount.getFrequentChampionId3Flex()))
			.build();
	}

	public void updateByRiotAccount(RiotAccount riotAccount) {
		this.actualUserId = riotAccount.getMember().getId();
		this.name = riotAccount.getName();
		this.tagLine = riotAccount.getTagLine();
		this.internalTagName = riotAccount.getInternalTagName();
		this.profileIconId = riotAccount.getIconId();
		this.puuid = riotAccount.getPuuid();
		// 솔로 랭크
		this.tier = riotAccount.getQueue();
		this.division = riotAccount.getDivision();
		this.lp = riotAccount.getLp();
		this.mmr = riotAccount.getMmr();
		this.mostLanes = toListMostItems(riotAccount.getFrequentLane1(), riotAccount.getFrequentLane2());
		this.mostChampions = toListMostItems(riotAccount.getFrequentChampionId1(), riotAccount.getFrequentChampionId2(),
			riotAccount.getFrequentChampionId3());
		// 자유 랭크
		this.tierFlex = riotAccount.getQueueFlex();
		this.divisionFlex = riotAccount.getDivisionFlex();
		this.lpFlex = riotAccount.getLpFlex();
		this.mmrFlex = riotAccount.getMmrFlex();
		this.mostLanesFlex = toListMostItems(riotAccount.getFrequentLane1Flex(), riotAccount.getFrequentLane2Flex());
		this.mostChampionsFlex = toListMostItems(riotAccount.getFrequentChampionId1Flex(),
			riotAccount.getFrequentChampionId2Flex(), riotAccount.getFrequentChampionId3Flex());

	}

	public void updateStatus(Status status) {
		this.status = status;
	}


	private static <T> List<T> toListMostItems(T... items) {
		List<T> mostItems = new ArrayList<>(Arrays.asList(items));
		mostItems.removeIf(Objects::isNull);
		return mostItems;
	}


}