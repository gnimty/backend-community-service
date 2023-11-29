package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.*;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.client5.http.auth.AuthStateCacheable;
import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
@Slf4j
@SpringBootTest
@ActiveProfiles(value = "local")
public class StompServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StompService stompService;

    @Autowired
    private UserService userService;


    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }


    @DisplayName("유저의 접속상태 수정")
    @Nested
    class updateConnectStatus {

        @DisplayName("변경한 접속상태로 수정 성공")
        @Test
        void successUpdateConnectStatus() {
            // given
            User user = User.builder()
                .actualUserId(1L)
                .tier(Tier.gold)
                .division(3)
                .summonerName("uni")
                .status(Status.ONLINE)
                .lp(3L).build();
            userRepository.save(user);

            // when
            stompService.updateConnStatus(user, Status.OFFLINE);

            // then
            User findUser = userService.getUser(1L);
            assertThat(findUser.getStatus()).isEqualTo(Status.OFFLINE);
        }
    }

}

 **/