package com.gnimty.communityapiserver.domain.chat.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.domain.chat.repository.User.UserRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
@ActiveProfiles(value = "local")
class UserServiceTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private MemberRepository memberRepository;


	@Autowired
	private UserService userService;


	@AfterEach
	void deleteAll() {
		userRepository.deleteAll();
		memberRepository.deleteAll();
	}


	@DisplayName("member로 user 조회")
	@Nested
	class getUserByMember {

		@DisplayName("member id와 동일한 actualUserId가 존재 시 user 조회 성공")
		@Test
		void successGetUserByMember() {
			// given
			Member member = new Member(true, "aaa@naver.com", "asdD12!", 1L, "uni", Status.OFFLINE,
				3L);
			memberRepository.save(member);

			User user = User.builder()
				.actualUserId(member.getId())
				.tier(Tier.gold)
				.division(3)
				.name("uni")
				.tagLine("tag")
				.status(Status.ONLINE)
				.lp(3L).build();
			userRepository.save(user);

			// when
			User findUser = userService.getUserByMember(member);

			// then
			assertThat(findUser).isEqualTo(user);
		}


		@DisplayName("member id와 동일한 actualUserId가 없을 시 실패")
		@Test
		void failGetUserByMember() {
			// given
			Member member = new Member(true, "aaa@naver.com", "asdD12!", 1L, "uni2", Status.OFFLINE,
				3L);
			memberRepository.save(member);

			// when & then
			assertThatThrownBy(() -> userService.getUserByMember(member))
				.isInstanceOf(BaseException.class)
				.satisfies(exception -> {
					assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
						ErrorCode.NOT_FOUND_CHAT_USER);
				})
				.hasMessageContaining(ErrorMessage.NOT_FOUND_CHAT_USER);

		}
	}


	@DisplayName("member id로 user 조회")
	@Nested
	class getUserByMemberId {

		@DisplayName("member id와 동일한 actualUserId가 존재 시 user 조회 성공")
		@Test
		void successGetUserByMember() {
			// given
			Member member = new Member(true, "aaa@naver.com", "asdD12!", 1L, "uni", Status.OFFLINE,
				3L);
			memberRepository.save(member);

			User user = User.builder()
				.actualUserId(member.getId())
				.tier(Tier.gold)
				.division(3)
				.name("uni")
				.tagLine("tag")
				.status(Status.ONLINE)
				.lp(3L).build();
			userRepository.save(user);

			// when
			User findUser = userService.getUser(member.getId());

			// then
			assertThat(findUser).isEqualTo(user);
		}

		@DisplayName("member id와 동일한 actualUserId가 없을 시 실패")
		@Test
		void failGetUserByMember() {
			// given
			Member member = new Member(true, "aaa@naver.com", "asdD12!", 1L, "uni2", Status.OFFLINE,
				3L);
			memberRepository.save(member);

			// when & then
			assertThatThrownBy(() -> userService.getUser(member.getId()))
				.isInstanceOf(BaseException.class)
				.satisfies(exception -> {
					assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
						ErrorCode.NOT_FOUND_CHAT_USER);
				})
				.hasMessageContaining(ErrorMessage.NOT_FOUND_CHAT_USER);
		}
	}


	@DisplayName("모든 user 조회")
	@Nested
	class findAllUsers {

		@DisplayName("저장한 모든 user 조회 성공")
		@Test
		void getAllUser() {
			// given
			List<User> users = new ArrayList<>();
			for (Integer i = 1; i <= 6; i++) {
				User user = User.builder()
					.actualUserId(i.longValue())
					.tier(Tier.diamond)
					.lp(1L)
					.status(Status.ONLINE)
					.name("name" + i)
					.tagLine("tag")
					.profileIconId(1L)
					.division(3)
					.build();
				users.add(user);
				userRepository.save(user);
			}

			// when
			List<User> findUsers = userService.findAllUser();

			// then
			assertThat(users).isEqualTo(findUsers);
		}
	}


	@DisplayName("user 저장")
	@Nested
	class saveUser {

		@DisplayName("RiotAccount로 새로 생성")
		@Test
		void createUserByRiotAccount() {
			// given
			Member member = new Member(true, "uni@naver.com", "afD23!", 1L, "uni", Status.ONLINE,
				1L);
			memberRepository.save(member);

			RiotAccount riotAccount = RiotAccount.builder()
				.member(member)
				.name("uni")
				.tagLine("tagLine")
				.internalTagName("uniInternalTagLine")
				.puuid("puuid")
				.build();

			// when
			userService.save(riotAccount);

			// then
			Optional<User> optionalUser = userRepository.findByActualUserId(member.getId());
			assertThat(optionalUser)
				.isPresent()
				.satisfies(findUser -> assertThat(findUser.get().getName()).isEqualTo(riotAccount.getName()));
		}

		@DisplayName("user가 이미 있는 상태에서 riotAccount를 저장하면 기존의 데이터 덮어쓰기")
		@Test
		void updateByRiotAccount() {
			// given
			Member member = new Member(true, "uni@naver.com", "afD23!", 1L, "uni", Status.ONLINE,
				1L);
			memberRepository.save(member);

			userRepository.save(User.builder()
				.actualUserId(member.getId())
				.tier(Tier.diamond)
				.lp(1L)
				.status(Status.ONLINE)
				.name("uni")
				.tagLine("tag")
				.profileIconId(1L)
				.division(3)
				.build());

			// when
			RiotAccount riotAccount = RiotAccount.builder()
				.member(member)
				.frequentChampionId1(1L)
				.frequentChampionId2(2L)
				.frequentChampionId3(3L)
				.frequentLane1(Lane.BOTTOM)
				.frequentLane2(Lane.JUNGLE)
				.build();
			User savedUser = userService.save(riotAccount);

			// then
			ArrayList<Lane> lanes = new ArrayList<>(Arrays.asList(Lane.BOTTOM, Lane.JUNGLE));

			assertThat(savedUser.getMostLanes()).containsAll(lanes);
			assertThat(savedUser.getActualUserId()).isEqualTo(member.getId());
			assertThat(savedUser.getStatus()).isEqualTo(Status.ONLINE);
		}


		@DisplayName("user가 이미 있는 상태에서 user를 저장하면 기존의 데이터 덮어쓰기")
		@Test
		void successUpdateUser() {
			// given
			User user = User.builder()
				.actualUserId(1L)
				.tier(Tier.diamond)
				.lp(1L)
				.status(Status.ONLINE)
				.name("uni")
				.tagLine("tag")
				.profileIconId(1L)
				.division(3)
				.build();
			userRepository.save(user);

			user.updateStatus(Status.AWAY);

			// when
			userService.save(user);

			// then
			User findUser = userRepository.findByActualUserId(1L).get();
			assertThat(findUser.getTier()).isEqualTo(Tier.diamond);
			assertThat(findUser.getName()).isEqualTo("uni");
			assertThat(findUser.getStatus()).isEqualTo(Status.AWAY);
		}

		@DisplayName("user가 없는 상태에서 user를 저장하면 실패")
		@Test
		void failUpdateUser() {
			// given
			User user = User.builder()
				.actualUserId(1L)
				.tier(Tier.diamond)
				.lp(1L)
				.status(Status.ONLINE)
				.name("uni")
				.tagLine("tag")
				.profileIconId(1L)
				.division(3)
				.build();

			// when & then
			assertThatThrownBy(() -> userService.save(user))
				.isInstanceOf(BaseException.class)
				.satisfies(exception -> {
					assertThat(((BaseException) exception).getErrorCode()).isEqualTo(
						ErrorCode.NOT_FOUND_CHAT_USER);
				})
				.hasMessageContaining(ErrorMessage.NOT_FOUND_CHAT_USER);
		}
	}


}