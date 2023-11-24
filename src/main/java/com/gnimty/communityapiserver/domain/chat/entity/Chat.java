package com.gnimty.communityapiserver.domain.chat.entity;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Status;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("chat")
@Getter
@NoArgsConstructor
@EqualsAndHashCode(exclude = "_id")
public class Chat {

	@Id
	private String id;
	private Long chatRoomNo;
	private Long senderId;
	private String message;
	private Date sendDate;
	private Integer readCnt = 1;

	@Builder
	public Chat(Long chatRoomNo, Long senderId, String message, Date sendDate) {
		this.chatRoomNo = chatRoomNo;
		this.senderId = senderId;
		this.message = message;
		this.sendDate = sendDate;
		// chat을 보내면 readCnt는 항상 1로 생성됨
	}

	public void readByAllUser () {
		this.readCnt = 0;
	}
}
