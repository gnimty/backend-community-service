package com.gnimty.communityapiserver.domain.chat.entity;


import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
		return User.builder()
			.actualUserId(riotAccount.getMember().getId())
			.profileIconId(riotAccount.getIconId())
			.tier(riotAccount.getQueue())
			.division(riotAccount.getDivision())
			.summonerName(riotAccount.getSummonerName())
			.mostLanes(getMostLanes(riotAccount))
			.mostChampions(getMostChampions(riotAccount))
			.lp(riotAccount.getLp())
			.build();
	}


	public void updateByRiotAccount(RiotAccount riotAccount) {
		this.actualUserId = riotAccount.getMember().getId();
		this.profileIconId = riotAccount.getIconId();
		this.tier = riotAccount.getQueue();
		this.summonerName = riotAccount.getSummonerName();
		this.lp = riotAccount.getLp();
		this.division = riotAccount.getDivision();
		this.mostChampions = getMostChampions();
		this.mostLanes = getMostLanes();
	}


	public void updateByUser(User updatedUser) {
		this.profileIconId = Optional.ofNullable(updatedUser.getProfileIconId()).orElse(this.profileIconId);
		this.tier = Optional.ofNullable(updatedUser.getTier()).orElse(this.tier);
		this.summonerName = Optional.ofNullable(updatedUser.getSummonerName()).orElse(this.summonerName);
		this.lp = Optional.ofNullable(updatedUser.getLp()).orElse(this.lp);
		this.division = Optional.ofNullable(updatedUser.getDivision()).orElse(this.division);
		this.status = Optional.ofNullable(updatedUser.getStatus()).orElse(this.status);
	}


	public void updateStatus(Status status) {
		this.status = status;
	}


	private static List<Long> getMostChampions(RiotAccount riotAccount) {
		List<Long> mostChampions = new ArrayList<> (List.of(
			riotAccount.getFrequentChampionId1(),
			riotAccount.getFrequentChampionId2(),
			riotAccount.getFrequentChampionId3()));

		mostChampions.removeIf(champion -> champion==null);
		return mostChampions;
	}


	private static List<Lane> getMostLanes(RiotAccount riotAccount) {
		List<Lane> mostLanes = new ArrayList<> (List.of(
			riotAccount.getFrequentLane1(), riotAccount.getFrequentLane2()));

		mostLanes.removeIf(lane -> lane==null);
		return mostLanes;
	}

}