package com.gnimty.communityapiserver.domain.chat.entity;


import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
public class User {
	@Id
	private String id;

	@Indexed(unique=true)
	private Long actualUserId;

	private Long profileIconId;
	private Tier tier;
	private Integer division;
	private Long lp;
	private String summonerName;
	private Status status;

	private List<Lane> mostLanes;
	private List<Long> mostChampions;

	public static User toUser(RiotAccount riotAccount){
		List<Lane> mostLanes = new ArrayList<> (List.of(
			riotAccount.getFrequentLane1(), riotAccount.getFrequentLane2()));
		List<Long> mostChampions = new ArrayList<> (List.of(
			riotAccount.getFrequentChampionId1(),
			riotAccount.getFrequentChampionId2(),
			riotAccount.getFrequentChampionId3()));

		mostLanes.removeIf(lane -> lane==null);
		mostChampions.removeIf(champion -> champion==null);

		return User.builder()
			.actualUserId(riotAccount.getMember().getId())
			.profileIconId(riotAccount.getIconId())
			.tier(riotAccount.getQueue())
			.division(riotAccount.getDivision())
			.summonerName(riotAccount.getSummonerName())
			.mostLanes(mostLanes)
			.mostChampions(mostChampions)
			.lp(riotAccount.getLp())
			.build();
	}

	public static User toUserWithId(RiotAccount riotAccount, String userId){
		List<Lane> mostLanes = new ArrayList<> (List.of(
			riotAccount.getFrequentLane1(), riotAccount.getFrequentLane2()));
		List<Long> mostChampions = new ArrayList<> (List.of(
			riotAccount.getFrequentChampionId1(),
			riotAccount.getFrequentChampionId2(),
			riotAccount.getFrequentChampionId3()));

		mostLanes.removeIf(lane -> lane==null);
		mostChampions.removeIf(champion -> champion==null);

		return User.builder()
			.id(userId)
			.actualUserId(riotAccount.getMember().getId())
			.profileIconId(riotAccount.getIconId())
			.tier(riotAccount.getQueue())
			.division(riotAccount.getDivision())
			.summonerName(riotAccount.getSummonerName())
			.mostLanes(mostLanes)
			.mostChampions(mostChampions)
			.lp(riotAccount.getLp())
			.build();
	}


}