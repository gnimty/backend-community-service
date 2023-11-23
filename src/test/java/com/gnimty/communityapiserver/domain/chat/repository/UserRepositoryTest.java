package com.gnimty.communityapiserver.domain.chat.repository;


import static org.assertj.core.api.Assertions.*;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.Chat.ChatRepository;
import com.gnimty.communityapiserver.domain.chat.repository.ChatRoom.ChatRoomRepository;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;

/**
@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class UserRepositoryTest {

	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChatRoomRepository chatRoomRepository;

	void clear() {
		chatRoomRepository.deleteAll();
		userRepository.deleteAll();
		chatRepository.deleteAll();
	}

	@Test
	void 유저생성_테스트() {

		clear();
		// 1. 유저 한명 생성 테스트
		User user = new User(null, 1L, 3L, Tier.diamond, 3, "so1omon", Status.ONLINE);
		List<User> all = userRepository.findAll();

		System.out.println("저장 전");
		for (User user1 : all) {
			System.out.println("user1 = " + user1);
		}
		User save = userRepository.save(user);
		all = userRepository.findAll();

		System.out.println("저장 후");
		for (User user1 : all) {
			System.out.println("user1 = " + user1);
		}

	}

	@Test
	void 같은아이디_유저두명_테스트() {

		clear();
		// 1. 유저 한명 생성 테스트
		User user1 = new User(null, 1L, 3L, Tier.diamond, 3, "so1omon", Status.ONLINE);
		User user2 = new User(null, 1L, 3L, Tier.diamond, 3, "so1omon", Status.ONLINE);
		assertThatThrownBy(() -> userRepository.saveAll(List.of(user1, user2)))
				.isInstanceOf(DuplicateKeyException.class);
	}

//	@AfterEach
//	void afterEach() {
//		userRepository.deleteAll();
//	}
//
//	@Autowired
//	UserRepository userRepository;
//
//	@Test
//	void saveUser() {
//
//		// given
//		User user = new User(1234L);
//
//		// when
//		userRepository.save(user);
//
//		// then
//		Optional<User> findUser = userRepository.findByUserId(user.getUserId());
//		assertThat(findUser).isNotEmpty();
//	}
//
//	@Test
//	void updateUser() {
//
//		// given
//		User user = new User(2345L);
//		user.updateUser("A", Status.ONLINE);
//		userRepository.save(user);
//
//		//when
//		User findUser = userRepository.findByUserId(2345L).orElse(null);
//		findUser.updateUser("B", Status.OFFLINE);
//		userRepository.save(findUser);
//
//		// then
//		User changedUser = userRepository.findByUserId(2345L).orElse(null);
//		assertThat(changedUser.getNickname()).isEqualTo("B");
//		assertThat(changedUser.getStatus()).isEqualTo(Status.OFFLINE);
//	}
//
//	@Test
//	void deleteUser() {
//		// given
//		User user = new User(2345L);
//		user.updateUser("A", Status.ONLINE);
//		userRepository.save(user);
//
//		// when
//		User findUser = userRepository.findByUserId(2345L).orElse(null);
//		userRepository.delete(findUser);
//
//		// then
//		Optional<User> deletedUser = userRepository.findByUserId(2345L);
//		assertThat(deletedUser).isEmpty();
//	}

}**/