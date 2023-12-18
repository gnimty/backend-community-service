package com.gnimty.communityapiserver.service.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.gnimty.communityapiserver.domain.block.entity.Block;
import com.gnimty.communityapiserver.domain.block.repository.BlockRepository;
import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.domain.member.service.MemberService;
import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateMainServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordEmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotAccountEntry;
import com.gnimty.communityapiserver.domain.member.service.utils.GoogleOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.KakaoOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.MailSenderUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.RiotOauthUtil;
import com.gnimty.communityapiserver.domain.memberlike.entity.MemberLike;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.domain.memberlike.service.MemberLikeReadService;
import com.gnimty.communityapiserver.domain.oauthinfo.entity.OauthInfo;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.domain.oauthinfo.service.OauthInfoReadService;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.prefergamemode.repository.PreferGameModeRepository;
import com.gnimty.communityapiserver.domain.prefergamemode.service.PreferGameModeReadService;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.domain.riotaccount.service.RiotAccountReadService;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleReadService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.service.ServiceTestSupport;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

public class MemberServiceTest extends ServiceTestSupport {

	@Autowired
	private RiotAccountRepository riotAccountRepository;
	@Autowired
	private OauthInfoRepository oauthInfoRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private PreferGameModeRepository preferGameModeRepository;
	@Autowired
	private MemberLikeRepository memberLikeRepository;
	@Autowired
	private BlockRepository blockRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;
	@Autowired
	private MemberService memberService;
	@Autowired
	private IntroductionRepository introductionRepository;
	@MockBean
	private RiotOauthUtil riotOauthUtil;
	@MockBean
	private RiotAccountReadService riotAccountReadService;
	@MockBean
	private KakaoOauthUtil kakaoOauthUtil;
	@MockBean
	private GoogleOauthUtil googleOauthUtil;
	@MockBean
	private OauthInfoReadService oauthInfoReadService;
	@MockBean
	private IntroductionReadService introductionReadService;
	@MockBean
	private ScheduleReadService scheduleReadService;
	@MockBean
	private PreferGameModeReadService preferGameModeReadService;
	@MockBean
	private StringRedisTemplate redisTemplate;
	@MockBean
	private PasswordEncoder passwordEncoder;
	@MockBean
	private MemberReadService memberReadService;
	@MockBean
	private MemberLikeReadService memberLikeReadService;
	@MockBean
	private MailSenderUtil mailSenderUtil;

	@DisplayName("rso 연동 시")
	@Nested
	class SummonerAccountLink {

	}

	@DisplayName("Oauth 추가 연동 시")
	@Nested
	class OauthAdditionalLink {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname1"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			oauthInfoRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("Provider에 따라서 OauthInfo entity가 저장된다.")
		@ParameterizedTest
		@EnumSource(value = Provider.class)
		@Transactional
		void should_saveEntity_AccordingToProvider(Provider provider) {

			// given
			String email = "email@email.com";
			OauthLoginServiceRequest request = createServiceRequest();

			// stub
			if (provider.equals(Provider.KAKAO)) {
				given(kakaoOauthUtil.getKakaoUserEmail(any(String.class)))
					.willReturn(email);
			} else {
				given(googleOauthUtil.getGoogleUserEmail(any(String.class)))
					.willReturn(email);
			}
			willDoNothing()
				.given(oauthInfoReadService)
				.throwIfExistsByEmailAndProvider(any(String.class), any(Provider.class));

			// when
			memberService.oauthAdditionalLink(provider, request);

			// then
			OauthInfo oauthInfo = oauthInfoRepository.findByMember(member).get(0);

			assertThat(oauthInfo.getMember()).isEqualTo(member);
			assertThat(oauthInfo.getProvider()).isEqualTo(provider);
		}

		@DisplayName("이미 해당 provider에 저장된 정보가 있으면 예외를 반환한다.")
		@ParameterizedTest
		@EnumSource(value = Provider.class)
		void should_throwException_alreadyExist(Provider provider) {

			// given
			BaseException exception = new BaseException(ErrorCode.ALREADY_LINKED_OAUTH);
			OauthLoginServiceRequest request = createServiceRequest();

			// stub
			given(oauthInfoReadService.existsByMemberAndProvider(
				any(Member.class), any(Provider.class)))
				.willReturn(true);

			// when & then
			assertThatThrownBy(() -> memberService.oauthAdditionalLink(provider, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("다른 회원이 같은 provider, email로 이미 연결돼 있으면 예외를 반환한다.")
		@ParameterizedTest
		@EnumSource(value = Provider.class)
		void should_throwException_alreadyExistAnotherMember(Provider provider) {

			// given
			String email = "email@email.com";
			BaseException exception = new BaseException(ErrorCode.ALREADY_LINKED_OAUTH);
			OauthLoginServiceRequest request = createServiceRequest();

			// stub
			given(oauthInfoReadService.existsByMemberAndProvider(any(Member.class),
				any(Provider.class)))
				.willReturn(false);
			willThrow(exception)
				.given(oauthInfoReadService)
				.throwIfExistsByEmailAndProvider(any(String.class), any(Provider.class));
			if (provider.equals(Provider.KAKAO)) {
				given(kakaoOauthUtil.getKakaoUserEmail(any(String.class)))
					.willReturn(email);
			} else {
				given(googleOauthUtil.getGoogleUserEmail(any(String.class)))
					.willReturn(email);
			}

			// when & then
			assertThatThrownBy(() -> memberService.oauthAdditionalLink(provider, request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		private OauthLoginServiceRequest createServiceRequest() {
			return OauthLoginServiceRequest.builder()
				.authCode("authCode")
				.build();
		}
	}

	@DisplayName("내 프로필 조회 시")
	@Nested
	class GetMyProfile {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			riotAccountRepository.deleteAllInBatch();
			introductionRepository.deleteAllInBatch();
			scheduleRepository.deleteAllInBatch();
			preferGameModeRepository.deleteAllInBatch();
			oauthInfoRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("저장된 회원 관련 정보들이 조회된다.")
		@Test
		void should_selectInfo_when_invokeGetMyProfile() {
			// given
			RiotAccount riotAccount = riotAccountRepository.save(
				createRiotAccount(member, "name", "puuid", true));
			introductionRepository.save(createIntroduction(member, true));
			Schedule schedule = scheduleRepository.save(createSchedule(member));
			PreferGameMode preferGameMode = preferGameModeRepository.save(
				createPreferGameMode(member));
			OauthInfo oauthInfo = oauthInfoRepository.save(createOauthInfo(member));

			// stub
			given(riotAccountReadService.findByMember(any(Member.class)))
				.willReturn(riotAccountRepository.findByMember(member));
			given(introductionReadService.findByMember(any(Member.class)))
				.willReturn(introductionRepository.findByMember(member));
			given(scheduleReadService.findByMember(any(Member.class)))
				.willReturn(scheduleRepository.findByMember(member));
			given(preferGameModeReadService.findByMember(any(Member.class)))
				.willReturn(preferGameModeRepository.findByMember(member));
			given(oauthInfoReadService.findByMember(any(Member.class)))
				.willReturn(oauthInfoRepository.findByMember(member));

			// when
			MyProfileServiceResponse response = memberService.getMyProfile();

			// then
			assertCommonInfos(response);
			assertOauthInfo(oauthInfo, response);
			assertCommonRiotDependentInfos(response);
			assertIntroduction(response);
			assertSchedule(schedule, response);
			assertPreferGameMode(preferGameMode, response);
			assertRiotAccount(riotAccount, response);
		}

		@DisplayName("rso에 연동되지 않으면 제한된 정보가 조회된다.")
		@Test
		void should_selectRestrictedInfo_when_notLinkedRso() {
			// given
			OauthInfo oauthInfo = oauthInfoRepository.save(createOauthInfo(member));

			// stub
			given(oauthInfoReadService.findByMember(any(Member.class)))
				.willReturn(oauthInfoRepository.findByMember(member));

			// when
			MyProfileServiceResponse response = memberService.getMyProfile();

			// then
			assertCommonInfos(response);
			assertOauthInfo(oauthInfo, response);
			assertThat(response.getRiotDependentInfo().getStatus()).isEqualTo(member.getStatus());
			assertThat(response.getRiotDependentInfo().getIsLinked()).isFalse();
			assertThat(response.getRiotDependentInfo().getIntroductions()).isEmpty();
			assertThat(response.getRiotDependentInfo().getSchedules()).isEmpty();
			assertThat(response.getRiotDependentInfo().getPreferGameModes()).isEmpty();
			assertThat(response.getRiotDependentInfo().getRiotAccounts()).isEmpty();
		}

		private void assertCommonRiotDependentInfos(MyProfileServiceResponse response) {
			assertThat(response.getRiotDependentInfo().getStatus()).isEqualTo(member.getStatus());
			assertThat(response.getRiotDependentInfo().getIsLinked()).isTrue();
		}

		private void assertCommonInfos(MyProfileServiceResponse response) {
			assertThat(response.getId()).isEqualTo(member.getId());
			assertThat(response.getEmail()).isEqualTo(member.getEmail());
			assertThat(response.getNickname()).isEqualTo(member.getNickname());
			assertThat(response.getUpCount()).isEqualTo(member.getUpCount());
			assertThat(response.getFavoriteChampionId()).isEqualTo(member.getFavoriteChampionID());
		}

		private void assertOauthInfo(OauthInfo oauthInfo, MyProfileServiceResponse response) {
			assertThat(response.getOauthInfos().get(0).getEmail()).isEqualTo(oauthInfo.getEmail());
			assertThat(response.getOauthInfos().get(0).getProvider())
				.isEqualTo(oauthInfo.getProvider());
		}

		private void assertIntroduction(MyProfileServiceResponse response) {
			IntroductionEntry introductionEntry = response.getRiotDependentInfo().getIntroductions()
				.get(0);
			assertThat(introductionEntry.getContent()).isEqualTo(introductionEntry.getContent());
			assertThat(introductionEntry.getId()).isEqualTo(introductionEntry.getId());
			assertThat(introductionEntry.getIsMain()).isEqualTo(introductionEntry.getIsMain());
		}

		private void assertSchedule(Schedule schedule, MyProfileServiceResponse response) {
			ScheduleEntry scheduleEntry = response.getRiotDependentInfo().getSchedules().get(0);
			assertThat(scheduleEntry.getDayOfWeek()).isEqualTo(schedule.getDayOfWeek());
			assertThat(scheduleEntry.getStartTime()).isEqualTo(schedule.getStartTime());
			assertThat(scheduleEntry.getEndTime()).isEqualTo(schedule.getEndTime());
		}

		private void assertPreferGameMode(PreferGameMode preferGameMode,
			MyProfileServiceResponse response) {
			assertThat(response.getRiotDependentInfo().getPreferGameModes().get(0).getGameMode())
				.isEqualTo(preferGameMode.getGameMode());
		}

		private void assertRiotAccount(RiotAccount riotAccount, MyProfileServiceResponse response) {
			RiotAccountEntry riotAccountEntry = response.getRiotDependentInfo()
				.getRiotAccounts().get(0);
			assertThat(riotAccountEntry.getId()).isEqualTo(riotAccount.getId());
			assertThat(riotAccountEntry.getName()).isEqualTo(riotAccount.getName());
			assertThat(riotAccountEntry.getLp()).isEqualTo(riotAccount.getLp());
			assertThat(riotAccountEntry.getDivision()).isEqualTo(riotAccount.getDivision());
			assertThat(riotAccountEntry.getMmr()).isEqualTo(riotAccount.getMmr());
			assertThat(riotAccountEntry.getPuuid()).isEqualTo(riotAccount.getPuuid());
			assertThat(riotAccountEntry.getIsMain()).isEqualTo(riotAccount.getIsMain());
			assertThat(riotAccountEntry.getIconId()).isEqualTo(riotAccount.getIconId());
			assertThat(riotAccountEntry.getQueue()).isEqualTo(riotAccount.getQueue());
			assertThat(riotAccountEntry.getFrequentChampionId1())
				.isEqualTo(riotAccount.getFrequentChampionId1());
			assertThat(riotAccountEntry.getFrequentChampionId2())
				.isEqualTo(riotAccount.getFrequentChampionId2());
			assertThat(riotAccountEntry.getFrequentChampionId3())
				.isEqualTo(riotAccount.getFrequentChampionId3());
			assertThat(riotAccountEntry.getFrequentLane1()).isEqualTo(
				riotAccount.getFrequentLane1());
			assertThat(riotAccountEntry.getFrequentLane2()).isEqualTo(
				riotAccount.getFrequentLane2());
		}
	}

	@DisplayName("회원 정보 변경 시")
	@Nested
	class UpdateMyProfileMain {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			given(memberReadService.findById(any(Long.class)))
				.willReturn(member);
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			introductionRepository.deleteAllInBatch();
			scheduleRepository.deleteAllInBatch();
			riotAccountRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("올바른 mainRiotAccountId를 입력하면 수정된다.")
		@Test
		void should_success_when_validRequest() {
			// given
			List<RiotAccount> riotAccounts = riotAccountRepository.saveAll(List.of(
				createRiotAccount(member, "name1", "puuid1", true),
				createRiotAccount(member, "name2", "puuid2", false)
			));
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.mainRiotAccountId(riotAccounts.get(1).getId())
				.build();

			// stub
			given(riotAccountReadService.findMainAccountByMember(any(Member.class)))
				.willReturn(riotAccounts.get(0));
			given(riotAccountReadService.findById(any(Long.class)))
				.willReturn(riotAccounts.get(1));

			// when
			memberService.updateMyProfileMain(request);

			// then
			assertThat(riotAccounts.get(0).getIsMain()).isFalse();
			assertThat(riotAccounts.get(1).getIsMain()).isTrue();
		}

		@DisplayName("올바른 status를 입력하면 수정된다.")
		@Test
		void should_success_when_validStatus() {
			// given
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.status(Status.OFFLINE)
				.build();

			// when
			memberService.updateMyProfileMain(request);

			// then
			assertThat(member.getStatus()).isEqualTo(request.getStatus());
		}

		/**
		 * 1개의 소개글(main) A가 존재하는 상태에서 소개글 C를 추가하고 A의 content를 수정하는데, 소개글 C는 main이 된다.
		 */
		@DisplayName("올바른 introduction을 입력하면 수정된다.")
		@Test
		void should_success_when_validIntroduction() {
			// given
			Introduction introductionA = introductionRepository.save(Introduction.builder()
				.content("A")
				.isMain(true)
				.member(member)
				.build());

			List<IntroductionEntry> introductionEntries = List.of(
				IntroductionEntry.builder()
					.id(introductionA.getId())
					.content("updatedA")
					.isMain(false)
					.build(),
				IntroductionEntry.builder()
					.content("C")
					.isMain(true)
					.build()
			);

			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(introductionEntries)
				.build();

			// stub
			given(introductionReadService.findById(introductionA.getId()))
				.willReturn(introductionA);
			given(introductionReadService.findByMember(any(Member.class)))
				.willReturn(List.of());
			willDoNothing()
				.given(introductionReadService)
				.throwIfExceedMain(any(Member.class));

			// when
			memberService.updateMyProfileMain(request);

			// then
			Optional<Introduction> insertedIntroduction = introductionRepository.findByMember(
					member)
				.stream()
				.filter(introduction -> introduction.getContent()
					.equals(request.getIntroductions().get(1).getContent()))
				.findFirst();
			assertThat(insertedIntroduction).isPresent();
			Introduction introduction = insertedIntroduction.get();
			assertThat(introductionA.getIsMain()).isFalse();
			assertThat(introductionA.getContent()).isEqualTo(
				introductionEntries.get(0).getContent());
			assertThat(introduction.getContent()).isEqualTo(
				request.getIntroductions().get(1).getContent());
		}

		@DisplayName("rso에 연동되지 않은 경우 예외를 반환한다.")
		@Test
		void should_returnException_when_notRsoLinked() {
			// given
			member.updateRsoLinked(false);

			BaseException exception = new BaseException(ErrorCode.NOT_LINKED_RSO);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(null))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("자신의 riot account id가 아니면 예외를 반환한다.")
		@Test
		void should_returnNoPermission_when_notIsMyselfRiotAccount() {
			// given
			Member newMember = memberRepository.save(
				createMemberByEmailAndNickname("email2@email2.com", "nicknam2"));
			RiotAccount riotAccount = riotAccountRepository.save(
				createRiotAccount(newMember, "name", "puuid", true));
			RiotAccount newRiotAccount = createRiotAccount(newMember, "name2", "puuid2", true);
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.mainRiotAccountId(100000000L)
				.build();
			BaseException exception = new BaseException(ErrorCode.NO_PERMISSION);

			// stub
			given(riotAccountReadService.findMainAccountByMember(any(Member.class)))
				.willReturn(riotAccount);
			given(riotAccountReadService.findById(any(Long.class)))
				.willReturn(newRiotAccount);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("자신의 introduction id가 아니면 예외를 반환한다.")
		@Test
		void should_returnNoPermission_when_notIsMyselfIntroduction() {
			// given
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.id(1000000000L)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.NO_PERMISSION);

			// stub
			Introduction introduction = mock(Introduction.class);
			Member mockMember = mock(Member.class);
			when(introduction.getMember()).thenReturn(mockMember);
			when(mockMember.getId()).thenReturn(10000000L);
			given(introductionReadService.findById(any(Long.class)))
				.willReturn(introduction);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("존재하지 않는 introduction id를 요청하면 실패한다.")
		@Test
		void should_returnNotFound_when_notExistIntroduction() {
			// given
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.id(1000000000L)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.INTRODUCTION_NOT_FOUND);

			// stub
			willThrow(exception)
				.given(introductionReadService)
				.findById(any(Long.class));

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("요청에 중복된 introduction id가 존재하면 실패한다.")
		@Test
		void should_fail_when_duplicatedId() {
			// given
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.id(1L)
						.build(),
					IntroductionEntry.builder()
						.id(1L)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.DUPLICATED_ID);

			// stub
			Introduction introduction = mock(Introduction.class);
			when(introduction.getMember()).thenReturn(member);
			given(introductionReadService.findById(any(Long.class)))
				.willReturn(introduction);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("요청에 isMain이 2개 이상이면 실패한다.")
		@Test
		void should_fail_when_isMainIsMany() {
			// given
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.isMain(true)
						.build(),
					IntroductionEntry.builder()
						.isMain(true)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.MAIN_CONTENT_MUST_BE_ONLY);

			// stub
			Introduction introduction = mock(Introduction.class);
			given(introductionReadService.findById(any(Long.class)))
				.willReturn(introduction);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("삽입, 수정 결과 introduction이 3개 초과이면 실패한다.")
		@Test
		void should_fail_when_introductionCountExceedThree() {
			// given
			Introduction introduction = introductionRepository.save(
				createIntroduction(member, true));
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.isMain(true)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.EXCEED_INTRODUCTION_COUNT);

			// stub
			given(introductionReadService.findById(any(Long.class)))
				.willReturn(introduction);
			List<Introduction> mockIntroductions = mock(List.class);
			when(mockIntroductions.size()).thenReturn(3);
			given(introductionReadService.findByMember(any(Member.class)))
				.willReturn(mockIntroductions);

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}

		@DisplayName("삽입, 수정 결과 main introduction이 1개 초과이면 실패한다.")
		@Test
		void should_fail_when_mainIntroductionCountExceedOne() {
			// given
			List<Introduction> introductions = introductionRepository.saveAll(List.of(
				createIntroduction(member, true),
				createIntroduction(member, false)
			));
			MyProfileUpdateMainServiceRequest request = MyProfileUpdateMainServiceRequest.builder()
				.introductions(List.of(
					IntroductionEntry.builder()
						.id(introductions.get(1).getId())
						.isMain(true)
						.build()))
				.build();
			BaseException exception = new BaseException(ErrorCode.MAIN_CONTENT_MUST_BE_ONLY);

			// stub
			given(introductionReadService.findById(any(Long.class)))
				.willReturn(introductions.get(0));
			willThrow(exception)
				.given(introductionReadService)
				.throwIfExceedMain(any(Member.class));

			// when & then
			assertThatThrownBy(() -> memberService.updateMyProfileMain(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}
	}

	@DisplayName("이메일 인증 코드 전송 시")
	@Nested
	class SendEmailAuthCode {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("zkfzpf56@naver.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("form 로그인 회원인 경우, 이메일이 전송된다.")
		@Test
		void should_sendEmail_when_formLoginMember() throws Exception {
			// given
			SendEmailServiceRequest request = SendEmailServiceRequest.builder()
				.email(member.getEmail())
				.build();

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			willDoNothing()
				.given(valueOperations)
				.set(any(String.class), any(String.class));
			given(valueOperations.getAndExpire(any(String.class), any(Long.class),
				any(TimeUnit.class)))
				.willReturn("value");
			willDoNothing()
				.given(mailSenderUtil)
				.sendEmail(any(String.class), any(String.class), any(String.class),
					any(String.class), any(String.class));
			given(memberReadService.findByEmailOrElseThrow(any(String.class),
				any(BaseException.class)))
				.willReturn(member);

			// when
			memberService.sendEmailAuthCode(request);

			// then
			then(mailSenderUtil)
				.should(times(1))
				.sendEmail(any(String.class), any(String.class), any(String.class),
					any(String.class), any(String.class));
			then(redisTemplate)
				.should(times(1))
				.expire(any(String.class), any(Long.class), any(TimeUnit.class));
		}

		@DisplayName("form 로그인 회원이 아닐 경우, 실패한다.")
		@Test
		void should_fail_when_notFormLoginMember() {
			// given
			SendEmailServiceRequest request = SendEmailServiceRequest.builder()
				.email("email")
				.build();
			Member newMember = createMemberByEmailAndNickname(null, "nickname");
			BaseException exception = new BaseException(ErrorCode.NOT_LOGIN_BY_FORM);

			// stub
			given(memberReadService.findByEmailOrElseThrow(any(String.class),
				any(BaseException.class)))
				.willReturn(newMember);

			// when & then
			assertThatThrownBy(() -> memberService.sendEmailAuthCode(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}
	}

	@DisplayName("이메일 인증 코드 검증 시")
	@Nested
	class VerifyEmailAuthCode {

		@BeforeEach
		void setUp() {
			Member member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("올바른 입력 코드를 요청하면 성공하며, uuid가 반환된다.")
		@Test
		void should_returnUUID_when_validRequest() {
			// given
			String authCode = "ABC123";
			PasswordEmailVerifyServiceRequest request = PasswordEmailVerifyServiceRequest.builder()
				.code(authCode)
				.build();
			UUID uuid = UUID.randomUUID();

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(valueOperations.get(any(String.class)))
				.willReturn(authCode);
			willDoNothing()
				.given(valueOperations)
				.set(any(String.class), any(String.class));
			given(valueOperations.getAndExpire(any(String.class), any(Long.class),
				any(TimeUnit.class)))
				.willReturn("verified");
			try (MockedStatic<UUID> ignored = mockStatic(UUID.class)) {
				// Stub
				given(UUID.randomUUID())
					.willReturn(uuid);

				// when
				PasswordEmailVerifyServiceResponse response = memberService.verifyEmailAuthCode(
					request);

				assertThat(response.getUuid()).isEqualTo(uuid.toString());
			}

			// then
			then(redisTemplate)
				.should(times(1))
				.expire(any(String.class), any(Long.class), any(TimeUnit.class));
			then(valueOperations)
				.should(times(1))
				.set(any(String.class), any(String.class));
			then(valueOperations)
				.should(times(1))
				.get(any(String.class));
			then(redisTemplate)
				.should(times(2))
				.opsForValue();
		}

		@DisplayName("올바르지 않은 인증 코드를 입력 시 실패한다.")
		@Test
		void should_fail_when_invalidAuthCode() {
			String savedCode = "ABC123";
			PasswordEmailVerifyServiceRequest request = PasswordEmailVerifyServiceRequest.builder()
				.code("ABC124")
				.build();
			BaseException exception = new BaseException(ErrorCode.INVALID_EMAIL_AUTH_CODE);

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(valueOperations.get(any(String.class)))
				.willReturn(savedCode);

			assertThatThrownBy(() -> memberService.verifyEmailAuthCode(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}
	}

	@DisplayName("비밀번호 재설정 시")
	@Nested
	class ResetPassword {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
		}

		@DisplayName("요청의 uuid가 올바르면 비밀번호가 변경된다.")
		@Test
		void should_updatePassword_when_uuidIsValid() {
			// given
			String uuid = "uuid";
			PasswordResetServiceRequest request = PasswordResetServiceRequest.builder()
				.email(member.getEmail())
				.uuid(uuid)
				.password("newPassword")
				.build();

			given(memberReadService.findByEmailOrElseThrow(any(String.class),
				any(BaseException.class)))
				.willReturn(member);
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(valueOperations.get(any(String.class)))
				.willReturn(uuid);
			given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
				.willReturn(true);
			given(passwordEncoder.encode(any(CharSequence.class)))
				.willReturn(request.getPassword());

			memberService.resetPassword(request);

			assertThat(member.getPassword()).isEqualTo(request.getPassword());
		}

		@DisplayName("요청의 uuid가 올바르지 않으면 예외를 반환한다.")
		@Test
		void should_returnException_when_uuidIsInvalid() {
			// given
			String uuid = "uuid";
			PasswordResetServiceRequest request = PasswordResetServiceRequest.builder()
				.email(member.getEmail())
				.uuid(uuid)
				.password("newPassword")
				.build();
			BaseException exception = new BaseException(ErrorCode.INVALID_UUID);

			given(memberReadService.findByEmailOrElseThrow(any(String.class),
				any(BaseException.class)))
				.willReturn(member);
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(valueOperations.get(any(String.class)))
				.willReturn("uuid2");
			given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
				.willReturn(false);

			assertThatThrownBy(() ->
				memberService.resetPassword(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
		}
	}

	@DisplayName("비밀번호 변경 시")
	@Nested
	class UpdatePassword {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("올바른 currentPassword를 입력하면 비밀번호가 변경된다.")
		@Test
		void should_updatePassword_when_currentPasswordIsValid() {
			// given
			PasswordUpdateServiceRequest request = PasswordUpdateServiceRequest.builder()
				.currentPassword(member.getPassword())
				.newPassword("newPassword")
				.build();

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(memberReadService.findById(any(Long.class)))
				.willReturn(member);
			given(valueOperations.get(any()))
				.willReturn("verified");
			given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
				.willReturn(true);
			given(passwordEncoder.encode(any(CharSequence.class)))
				.willReturn(request.getNewPassword());

			// when
			memberService.updatePassword(request);

			// then
			assertThat(request.getNewPassword()).isEqualTo(member.getPassword());
		}

		@DisplayName("올바르지 않은 currentPassword를 입력하면 비밀번호가 예외를 반환한다.")
		@Test
		void should_returnException_when_currentPasswordIsInvalid() {
			// given
			PasswordUpdateServiceRequest request = PasswordUpdateServiceRequest.builder()
				.currentPassword(member.getPassword())
				.newPassword("newPassword")
				.build();
			BaseException exception = new BaseException(ErrorCode.INVALID_PASSWORD);

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(memberReadService.findById(any(Long.class)))
				.willReturn(member);
			given(valueOperations.get(any()))
				.willReturn(null);
			given(passwordEncoder.matches(any(CharSequence.class), any(String.class)))
				.willReturn(false);

			// when & then
			assertThatThrownBy(() -> memberService.updatePassword(request))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
			then(passwordEncoder)
				.should(times(1))
				.matches(any(CharSequence.class), any(String.class));
		}
	}

	@DisplayName("oauth 정보 삭제 시")
	@Nested
	class DeleteOauthInfo {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("요청의 provider에 해당하는 OauthInfo가 존재할 경우, 삭제한다.")
		@ParameterizedTest
		@EnumSource(Provider.class)
		void should_deleteOauthInfo_when_oauthInfoExist(Provider provider) {
			// given
			OauthInfo oauthInfo = oauthInfoRepository.save(createOauthInfo(member));

			// stub
			given(oauthInfoReadService.findByMemberAndProvider(member, provider))
				.willReturn(oauthInfo);

			// when
			memberService.deleteOauthInfo(provider);

			// then
			assertThat(oauthInfoRepository.findByMemberAndProvider(member, provider)).isEmpty();
		}

		@DisplayName("form 로그인이 아닐 경우 oauth 정보는 삭제될 수 없다.")
		@ParameterizedTest
		@EnumSource(Provider.class)
		void should_failDeleteOauthInfo_when_notFormLogin(Provider provider) {
			// given
			MemberThreadLocal.set(memberRepository.save(
				createMemberByEmailAndNickname(null, "nickname2")));
			BaseException exception = new BaseException(ErrorCode.NOT_LOGIN_BY_FORM);

			// when & then
			assertThatThrownBy(() -> memberService.deleteOauthInfo(provider))
				.isInstanceOf(exception.getClass())
				.hasMessage(exception.getMessage());
			then(oauthInfoReadService)
				.shouldHaveNoInteractions();
		}
	}

	@DisplayName("로그아웃 시")
	@Nested
	class Logout {

		@BeforeEach
		void setUp() {
			Member member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("redis에 저장돼있던 Refresh token이 삭제된다.")
		@Test
		void should_deleteRefreshTokenInRedis_when_logout() {
			// given

			// stub
			ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
			given(redisTemplate.opsForValue())
				.willReturn(valueOperations);
			given(redisTemplate.delete(anyString()))
				.willReturn(true);

			// when
			memberService.logout();

			// then
			then(redisTemplate)
				.should(times(1))
				.delete(any(String.class));
		}
	}

	@DisplayName("회원탈퇴 시")
	@Nested
	class Withdrawal {

		private Member member;

		@BeforeEach
		void setUp() {
			member = memberRepository.save(
				createMemberByEmailAndNickname("email@email.com", "nickname"));
			MemberThreadLocal.set(member);
		}

		@AfterEach
		void tearDown() {
			blockRepository.deleteAllInBatch();
			memberLikeRepository.deleteAllInBatch();
			memberRepository.deleteAllInBatch();
			MemberThreadLocal.remove();
		}

		@DisplayName("회원 테이블과 연결된 모든 테이블에 회원에 대한 정보가 삭제 된다.")
		@Test
		void should_deleteAllInfoRelatedToMember_when_withdrawal() {
			// given
			Member newMember = memberRepository.save(
				createMemberByEmailAndNickname("email@email.comm", "nickname2"));
			memberLikeRepository.saveAll(List.of(
				MemberLike.builder().targetMember(member).sourceMember(newMember).build(),
				MemberLike.builder().targetMember(newMember).sourceMember(member).build()
			));
			riotAccountRepository.save(createRiotAccount(member, "name", "puuid", true));
			oauthInfoRepository.save(createOauthInfo(member));
			blockRepository.save(Block.builder()
				.blocker(member)
				.blocked(newMember)
				.build());
			scheduleRepository.save(createSchedule(member));
			preferGameModeRepository.save(createPreferGameMode(member));
			introductionRepository.save(createIntroduction(member, true));

			// when
			memberService.withdrawal();

			// then
			assertThat(memberLikeRepository.findBySourceMember(member)).isEmpty();
			assertThat(memberLikeRepository.findBySourceMemberAndTargetMember(newMember,
				member)).isEmpty();
			assertThat(riotAccountRepository.findByMember(member)).isEmpty();
			assertThat(oauthInfoRepository.findByMember(member)).isEmpty();
			assertThat(blockRepository.findByBlocker(member)).isEmpty();
			assertThat(scheduleRepository.findByMember(member)).isEmpty();
			assertThat(preferGameModeRepository.findByMember(member)).isEmpty();
			assertThat(introductionRepository.findByMember(member)).isEmpty();
			assertThat(memberRepository.findById(member.getId())).isEmpty();
		}
	}

	public Member createMemberByEmailAndNickname(String email, String nickname) {
		return Member.builder()
			.rsoLinked(true)
			.email(email)
			.password("Abc123**")
			.favoriteChampionID(1L)
			.nickname(nickname)
			.status(Status.ONLINE)
			.upCount(0L)
			.build();
	}

	public RiotAccount createRiotAccount(Member member, String summonerName, String puuid,
		Boolean isMain) {
		return RiotAccount.builder()
			.name(summonerName)
			.tagLine("tagLine")
			.internalTagName((summonerName + "#tagLine").toLowerCase().intern())
			.isMain(isMain)
			.queue(Tier.diamond)
			.lp(1000L)
			.division(1)
			.mmr(10000L)
			.frequentLane1(Lane.TOP)
			.frequentLane2(Lane.BOTTOM)
			.frequentChampionId1(1L)
			.frequentChampionId2(2L)
			.frequentChampionId3(3L)
			.puuid(puuid)
			.iconId(1L)
			.queueFlex(Tier.diamond)
			.lpFlex(1000L)
			.divisionFlex(1)
			.mmrFlex(10000L)
			.member(member)
			.build();
	}

	private Introduction createIntroduction(Member member, Boolean isMain) {
		return Introduction.builder()
			.content("content")
			.isMain(isMain)
			.member(member)
			.build();
	}

	private OauthInfo createOauthInfo(Member member) {
		return OauthInfo.builder()
			.email("email@email.com")
			.provider(Provider.KAKAO)
			.member(member)
			.build();
	}

	private Schedule createSchedule(Member member) {
		return Schedule.builder()
			.dayOfWeek(DayOfWeek.SUNDAY)
			.startTime(2)
			.endTime(23)
			.member(member)
			.build();
	}

	private PreferGameMode createPreferGameMode(Member member) {
		return PreferGameMode.builder()
			.gameMode(GameMode.RANK_SOLO)
			.member(member)
			.build();
	}
}
