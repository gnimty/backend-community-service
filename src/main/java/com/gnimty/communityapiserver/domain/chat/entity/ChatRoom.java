package com.gnimty.communityapiserver.domain.chat.entity;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chatRoom")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(exclude = "_id")
public class ChatRoom {

	@Id
	private String id;

	@Indexed(unique = true)
	private Long chatRoomNo;
	private List<Participant> participants;
	private Date createdDate;

	@Getter
	@Setter
	@AllArgsConstructor
	@ToString
	public static class Participant {

		@DBRef
		private User user;
		private Date exitDate;
		private Blocked status;
	}

	private Date lastModifiedDate;
}