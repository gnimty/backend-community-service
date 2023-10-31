package com.gnimty.communityapiserver.domain.chat.entity;


import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
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
public class User {
	@Id
	private String id;

	@Indexed(unique=true)
	private Long actualUserId;

	private Long profileIconId;
	private Tier tier;
	private Integer division;
	private String summonerName;
	private Status status;

	public static User toUser(RiotAccount riotAccount){
		return new User(
			null,
			riotAccount.getMember().getId(),
			riotAccount.getIconId(),
			riotAccount.getQueue(),
			riotAccount.getDivision(),
			riotAccount.getSummonerName(),
			riotAccount.getMember().getStatus());
	}
}