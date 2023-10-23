package com.gnimty.communityapiserver.domain.chat.entity;


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
@EqualsAndHashCode(exclude = "_id")
public class User {

	@Id
	private Long id;

	@Indexed(unique = true)
	private Long actualUserId;

	private Long profileIconId;
	private String tier;
	private Integer division;
	private String summonerName;
	private Status status;
}
