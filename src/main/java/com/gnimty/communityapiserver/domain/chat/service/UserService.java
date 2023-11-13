package com.gnimty.communityapiserver.domain.chat.service;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.mongodb.bulk.BulkWriteResult;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public List<User> findUser(){
        return userRepository.findAll();
    }

    public Optional<User> findUser(Long actualUserId){
        return userRepository.findByActualUserId(actualUserId);
    }

    public User getUser(Long actualUserId) {
        return findUser(actualUserId)
            .orElseThrow(()-> new BaseException(ErrorCode.NOT_FOUND_CHAT_USER));
    }

    public User getUserByMember(Member member) {
        return findUser(member.getId())
            .orElseThrow(()-> new BaseException(ErrorCode.NOT_FOUND_CHAT_USER));
    }

    public User save(RiotAccount riotAccount){
        return userRepository.save(User.toUser(riotAccount));
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public BulkWriteResult updateMany(List<User> users) {
        return userRepository.bulkUpdate(users);
    }
}
