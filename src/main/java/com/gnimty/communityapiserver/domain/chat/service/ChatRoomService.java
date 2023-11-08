package com.gnimty.communityapiserver.domain.chat.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import com.gnimty.communityapiserver.domain.chat.entity.AutoIncrementSequence;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom;
import com.gnimty.communityapiserver.domain.chat.entity.ChatRoom.Participant;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.service.dto.UserWithBlockDto;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Boolean checkChatRoom(Long chatRoomNo){
        return chatRoomRepository.existsByChatRoomNo(chatRoomNo);
    }

    public Optional<ChatRoom> findChatRoom(User me, User other){
        return chatRoomRepository.findByUsers(me, other);
    }

    public List<ChatRoom> findChatRoom(User user){
        return chatRoomRepository.findByUser(user);
    }


    // TODO solomon : 단일 채팅방 정보 가져오기
    public Optional<ChatRoom> findChatRoom(Long chatRoomNo){
        return chatRoomRepository.findByChatRoomNo(chatRoomNo);
    }

    public ChatRoom getChatRoom(Long chatRoomNo){
        return findChatRoom(chatRoomNo)
            .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    public ChatRoom getChatRoom(User me, User other){
        return findChatRoom(me, other)
            .orElseThrow(() -> new BaseException(ErrorCode.NOT_FOUND_CHAT_ROOM));
    }

    public ChatRoom save(UserWithBlockDto me, UserWithBlockDto other){
        Optional<ChatRoom> bothJoined = findChatRoom(me.getUser(), other.getUser());

        if (bothJoined.isPresent()){
            throw new BaseException(ErrorCode.CHATROOM_ALREADY_EXISTS);
        }

        List<Participant> participants = List.of(
            Participant.builder().user(me.getUser()).exitDate(null).blockedStatus(me.getStatus()).build(),
            Participant.builder().user(other.getUser()).exitDate(null).blockedStatus(other.getStatus()).build()
        );

        return chatRoomRepository.save(participants);
    }

    public void delete(Long chatRoomNo){
        chatRoomRepository.deleteByChatRoomNo(chatRoomNo);
    }

    public void update(ChatRoom chatRoom){
        chatRoomRepository.update(chatRoom);
    }
}