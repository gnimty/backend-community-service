package com.gnimty.communityapiserver.domain.chat.entity;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chat")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "_id")
public class Chat {

	@Id
	private Long id;
	private Long chatRoomNo;
	private Long senderId;
	private String message;
	private Date sendDate;
	private Integer readCnt;
}
