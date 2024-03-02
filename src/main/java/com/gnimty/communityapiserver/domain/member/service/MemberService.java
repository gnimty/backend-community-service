package com.gnimty.communityapiserver.domain.member.service;

import static com.gnimty.communityapiserver.global.constant.Bound.INITIAL_COUNT;
import static com.gnimty.communityapiserver.global.constant.Bound.MAIN_INTRODUCTION_COUNT;
import static com.gnimty.communityapiserver.global.constant.Bound.MAX_HOUR;
import static com.gnimty.communityapiserver.global.constant.Bound.MAX_INTRODUCTION_COUNT;
import static com.gnimty.communityapiserver.global.constant.Bound.MIN_HOUR;
import static com.gnimty.communityapiserver.global.constant.Bound.RANDOM_CODE_LENGTH;
import static com.gnimty.communityapiserver.global.constant.CacheType.REFRESH_TOKEN;
import static com.gnimty.communityapiserver.global.constant.CacheType.RESET_PASSWORD_EMAIL_CODE;
import static com.gnimty.communityapiserver.global.constant.CacheType.UPDATE_PASSWORD_CODE;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.PASSWORD_EMAIL_BANNER;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.PASSWORD_EMAIL_TEMPLATE;
import static com.gnimty.communityapiserver.global.constant.CommonStringType.TAG_SPLITTER;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.PASSWORD;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.REFRESH;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.UPDATE_PASSWORD;
import static com.gnimty.communityapiserver.global.utils.CacheService.getCacheKey;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateMainServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordEmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.MyProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OauthInfoEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PasswordEmailVerifyServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotAccountEntry;
import com.gnimty.communityapiserver.domain.member.service.dto.response.RiotDependentInfo;
import com.gnimty.communityapiserver.domain.member.service.utils.GoogleOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.KakaoOauthUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.MailSenderUtil;
import com.gnimty.communityapiserver.domain.member.service.utils.RiotOauthUtil;
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
import com.gnimty.communityapiserver.global.config.async.AfterRiotAccountCommitEvent;
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import com.gnimty.communityapiserver.global.constant.GameMode;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.dto.webclient.RiotAccountInfo;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.utils.CacheService;
import com.gnimty.communityapiserver.global.utils.RandomCodeGenerator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

	private final RiotOauthUtil riotOauthUtil;
	private final RiotAccountReadService riotAccountReadService;
	private final RiotAccountRepository riotAccountRepository;
	private final KakaoOauthUtil kakaoOauthUtil;
	private final GoogleOauthUtil googleOauthUtil;
	private final OauthInfoRepository oauthInfoRepository;
	private final OauthInfoReadService oauthInfoReadService;
	private final IntroductionReadService introductionReadService;
	private final ScheduleReadService scheduleReadService;
	private final PreferGameModeReadService preferGameModeReadService;
	private final IntroductionRepository introductionRepository;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final PreferGameModeRepository preferGameModeRepository;
	private final MemberReadService memberReadService;
	private final ScheduleRepository scheduleRepository;
	private final MailSenderUtil mailSenderUtil;
	private final ApplicationEventPublisher eventPublisher;
	private final CacheService cacheService;
	private final WithdrawalService withdrawalService;

	@Transactional
	public RiotAccount summonerAccountLink(OauthLoginServiceRequest request) {
		Member member = MemberThreadLocal.get();

		RiotAccountInfo info = riotOauthUtil.getPuuid(request.getAuthCode(), request.getRedirectUri());

		riotAccountReadService.throwIfExistsByPuuid(info.getPuuid());
		Boolean existsMain = riotAccountReadService.existsByMemberId(member);

		RiotAccount riotAccount = riotAccountRepository.save(RiotAccount.builder()
			.name(info.getGameName())
			.internalTagName((info.getGameName() + TAG_SPLITTER.getValue() + info.getTagLine()).trim().toLowerCase())
			.tagLine(info.getTagLine())
			.isMain(!existsMain)
			.puuid(info.getPuuid())
			.member(member)
			.level((long) INITIAL_COUNT.getValue())
			.build()
		);

		if (!existsMain) {
			member.updateNickname(riotAccount.getName() + TAG_SPLITTER.getValue() + riotAccount.getTagLine());
			member.updateRsoLinked(true);
			insertDefaultQueries(member);
		}

		eventPublisher.publishEvent(new AfterRiotAccountCommitEvent(info, riotAccount.getId()));
		return riotAccount;
	}

	public void oauthAdditionalLink(Provider provider, OauthLoginServiceRequest request) {
		final Member member = MemberThreadLocal.get();
		throwIfAlreadyLinkedProvider(member, provider);
		String userEmail;
		if (provider.equals(Provider.KAKAO)) {
			userEmail = kakaoOauthUtil.getKakaoUserEmail(request.getAuthCode(), request.getRedirectUri());
		} else {
			userEmail = googleOauthUtil.getGoogleUserEmail(request.getAuthCode(), request.getRedirectUri());
		}
		oauthInfoReadService.throwIfExistsByEmailAndProvider(userEmail, provider);
		createOauthInfoByEmail(member, userEmail, provider);
	}

	public MyProfileServiceResponse getMyProfile() {
		final Member member = MemberThreadLocal.get();
		RiotDependentInfo riotDependentInfo = getRiotDependentInfo(member);
		List<OauthInfo> oauthInfos = oauthInfoReadService.findByMember(member);
		List<OauthInfoEntry> oauthInfoEntries = oauthInfos.stream()
			.map(OauthInfoEntry::from)
			.toList();

		return getMyProfileServiceResponse(member, riotDependentInfo, oauthInfoEntries);
	}

	public RiotAccount updateMyProfileMain(MyProfileUpdateMainServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		RiotAccount riotAccount = updateMainRiotAccount(request, member);
		updateStatus(request.getStatus());
		updateIntroductions(request.getIntroductions(), member);
		memberRepository.save(member);
		return riotAccount;
	}

	public void sendEmailAuthCode(SendEmailServiceRequest request) {
		Member member = memberReadService.findByEmailOrElseThrow(
			request.getEmail(), new BaseException(ErrorCode.NOT_LOGIN_BY_FORM));
		if (member.getEmail() == null) {
			throw new BaseException(ErrorCode.NOT_LOGIN_BY_FORM);
		}

		String code = RandomCodeGenerator.generateCodeByLength(RANDOM_CODE_LENGTH.getValue());
		mailSenderUtil.sendEmail(Auth.EMAIL_SUBJECT.getContent(), member.getEmail(), code,
			PASSWORD_EMAIL_TEMPLATE.getValue(), PASSWORD_EMAIL_BANNER.getValue());
		String key = getCacheKey(PASSWORD, member.getEmail());
		cacheService.put(RESET_PASSWORD_EMAIL_CODE, key, code);
	}

	public PasswordEmailVerifyServiceResponse verifyEmailAuthCode(PasswordEmailVerifyServiceRequest request) {
		String emailAuthKey = getCacheKey(PASSWORD, request.getEmail());
		String savedCode = cacheService.get(RESET_PASSWORD_EMAIL_CODE, emailAuthKey);
		if (!request.getCode().equals(savedCode)) {
			throw new BaseException(ErrorCode.INVALID_EMAIL_AUTH_CODE);
		}
		UUID uuid = UUID.randomUUID();
		String passwordKey = getCacheKey(UPDATE_PASSWORD, request.getEmail());
		cacheService.put(UPDATE_PASSWORD_CODE, passwordKey, uuid.toString());
		cacheService.evict(RESET_PASSWORD_EMAIL_CODE, emailAuthKey);
		return PasswordEmailVerifyServiceResponse.builder()
			.uuid(uuid.toString())
			.build();
	}

	public void resetPassword(PasswordResetServiceRequest request) {

		String uuid = cacheService.get(UPDATE_PASSWORD_CODE, getCacheKey(UPDATE_PASSWORD, request.getEmail()));
		if (!request.getUuid().equals(uuid)) {
			throw new BaseException(ErrorCode.INVALID_UUID);
		}
		Member member = memberReadService.findByEmailOrElseThrow(request.getEmail(),
			new BaseException(ErrorCode.NOT_LOGIN_BY_FORM));
		member.updatePassword(passwordEncoder.encode(request.getPassword()));

		cacheService.evict(UPDATE_PASSWORD_CODE, request.getEmail());
	}

	public void updatePassword(PasswordUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (member.getEmail() == null) {
			throw new BaseException(ErrorCode.NOT_LOGIN_BY_FORM);
		}
		if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
			throw new BaseException(ErrorCode.INVALID_PASSWORD);
		}
		member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
		memberRepository.save(member);
	}

	public void updateMyProfile(MyProfileUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		updateStatus(request.getStatus());
		updateIntroductions(request.getIntroductions(), member);
		updatePreferGameMode(request.getPreferGameModes());
		updateSchedules(request.getSchedules());
	}

	public void updateStatus(Status status) {
		Member member = MemberThreadLocal.get();
		member.updateStatus(status);
		memberRepository.save(member);
	}

	public void updateStatus(Status status, Long memberId) {
		Member member = memberReadService.findById(memberId);
		member.updateStatus(status);
	}

	private void updatePreferGameMode(List<PreferGameModeEntry> preferGameModes) {
		if (preferGameModes == null) {
			return;
		}
		Member member = MemberThreadLocal.get();
		preferGameModeRepository.deleteByMember(member);
		preferGameModes.stream()
			.map(entry -> createPreferGameMode(entry, member))
			.forEach(preferGameModeRepository::save);
	}

	public void deleteOauthInfo(Provider provider) {
		Member member = MemberThreadLocal.get();
		if (member.getEmail() == null) {
			throw new BaseException(ErrorCode.NOT_LOGIN_BY_FORM);
		}
		OauthInfo oauthInfo = oauthInfoReadService.findByMemberAndProvider(member, provider);
		oauthInfoRepository.delete(oauthInfo);
	}

	public void logout() {
		Member member = MemberThreadLocal.get();
		cacheService.evict(REFRESH_TOKEN, getCacheKey(REFRESH, member.getId().toString()));
	}

	public void withdrawal() {
		Member member = MemberThreadLocal.get();
		withdrawalService.withdrawal(member.getId());
		cacheService.evict(REFRESH_TOKEN, getCacheKey(REFRESH, member.getId().toString()));
	}

	private void insertDefaultQueries(Member member) {
		for (GameMode gameMode : GameMode.values()) {
			preferGameModeRepository.save(PreferGameMode.builder()
				.member(member)
				.gameMode(gameMode)
				.build()
			);
		}
		for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
			scheduleRepository.save(Schedule.builder()
				.member(member)
				.dayOfWeek(dayOfWeek)
				.startTime(MIN_HOUR)
				.endTime(MAX_HOUR)
				.build()
			);
		}
	}

	private void updateIntroductions(List<IntroductionEntry> introductions, Member member) {
		if (introductions != null) {
			List<IntroductionEntry> updateIntroductions = introductions.stream()
				.filter(entry -> entry.getId() != null)
				.peek(entry -> validatePermission(entry.getId(), member))
				.peek(this::updateIntroduction)
				.toList();
			List<IntroductionEntry> insertIntroductions = introductions.stream()
				.filter(introductionEntry -> introductionEntry.getId() == null)
				.toList();

			throwIfDuplicatedId(updateIntroductions);
			throwIfDuplicatedIsMain(introductions);
			List<Introduction> existIntroductions = introductionReadService.findByMember(member);
			throwIfExceedIntroductionCount(existIntroductions.size(), insertIntroductions.size());
			insertIntroductions.forEach(entry -> saveIntroduction(member, entry));
			introductionReadService.throwIfExceedMain(member);
		}
	}

	private void saveIntroduction(Member member, IntroductionEntry entry) {
		introductionRepository.save(Introduction.builder()
			.member(member)
			.content(entry.getContent())
			.isMain(entry.getIsMain())
			.build());
	}

	private void updateIntroduction(IntroductionEntry entry) {
		Introduction findIntroduction = introductionReadService.findById(entry.getId());
		findIntroduction.updateContent(entry.getContent());
		findIntroduction.updateIsMain(entry.getIsMain());
	}

	private void validatePermission(Long id, Member member) {
		Introduction findIntroduction = introductionReadService.findById(id);
		if (!Objects.equals(member.getId(), findIntroduction.getMember().getId())) {
			throw new BaseException(ErrorCode.NO_PERMISSION);
		}
	}

	private void throwIfExceedIntroductionCount(int updateSize, int insertSize) {
		if (updateSize + insertSize > MAX_INTRODUCTION_COUNT.getValue()) {
			throw new BaseException(ErrorCode.EXCEED_INTRODUCTION_COUNT);
		}
	}

	private void throwIfDuplicatedIsMain(List<IntroductionEntry> introductions) {
		long mainCount = introductions.stream()
			.filter(IntroductionEntry::getIsMain)
			.count();
		if (mainCount > MAIN_INTRODUCTION_COUNT.getValue()) {
			throw new BaseException(ErrorCode.MAIN_CONTENT_MUST_BE_ONLY);
		}
	}

	private void throwIfDuplicatedId(List<IntroductionEntry> introductions) {
		long count = introductions.stream()
			.map(IntroductionEntry::getId)
			.distinct()
			.count();
		if (count != introductions.size()) {
			throw new BaseException(ErrorCode.DUPLICATED_ID);
		}
	}

	private void updateSchedules(List<ScheduleEntry> schedules) {
		if (schedules == null) {
			return;
		}
		Member member = MemberThreadLocal.get();
		scheduleRepository.deleteByMember(member);

		schedules.stream()
			.map(entry -> createSchedule(entry, member))
			.forEach(scheduleRepository::save);
	}

	private Schedule createSchedule(ScheduleEntry entry, Member member) {
		return Schedule.builder()
			.dayOfWeek(entry.getDayOfWeek())
			.startTime(entry.getStartTime())
			.endTime(entry.getEndTime())
			.member(member)
			.build();
	}

	private RiotAccount updateMainRiotAccount(MyProfileUpdateMainServiceRequest request, Member member) {
		if (request.getMainRiotAccountId() != null) {
			RiotAccount prevMainAccount = riotAccountReadService.findMainAccountByMember(member);
			RiotAccount postMainAccount = riotAccountReadService.findById(request.getMainRiotAccountId());
			if (!Objects.equals(postMainAccount.getMember().getId(), member.getId())) {
				throw new BaseException(ErrorCode.NO_PERMISSION);
			}
			prevMainAccount.updateIsMain();
			postMainAccount.updateIsMain();
			return postMainAccount;
		}
		return null;
	}

	private RiotDependentInfo getRiotDependentInfo(Member member) {
		List<RiotAccount> riotAccounts = riotAccountReadService.findByMember(member);
		List<Introduction> introductions = introductionReadService.findByMember(member);
		List<Schedule> schedules = scheduleReadService.findByMember(member);
		List<PreferGameMode> preferGameModes = preferGameModeReadService.findByMember(member);
		return RiotDependentInfo.builder()
			.isLinked(!riotAccounts.isEmpty())
			.status(member.getStatus())
			.introductions(introductions.stream().map(IntroductionEntry::from).toList())
			.schedules(schedules.stream().map(ScheduleEntry::from).toList())
			.preferGameModes(preferGameModes.stream().map(PreferGameModeEntry::from).toList())
			.riotAccounts(riotAccounts.stream().map(RiotAccountEntry::from).toList())
			.build();
	}

	private void createOauthInfoByEmail(Member member, String userEmail, Provider provider) {
		oauthInfoRepository.save(OauthInfo.builder()
			.provider(provider)
			.email(userEmail)
			.member(member)
			.build());
	}

	private MyProfileServiceResponse getMyProfileServiceResponse(
		Member member,
		RiotDependentInfo riotDependentInfo,
		List<OauthInfoEntry> oauthInfoEntries
	) {
		return MyProfileServiceResponse.builder()
			.id(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.favoriteChampionId(member.getFavoriteChampionID())
			.upCount(member.getUpCount())
			.riotDependentInfo(riotDependentInfo)
			.oauthInfos(oauthInfoEntries)
			.build();
	}

	private void throwIfAlreadyLinkedProvider(Member member, Provider provider) {
		if (oauthInfoReadService.existsByMemberAndProvider(member, provider)) {
			throw new BaseException(ErrorCode.ALREADY_LINKED_OAUTH);
		}
	}

	private PreferGameMode createPreferGameMode(PreferGameModeEntry entry, Member member) {
		return PreferGameMode.builder()
			.gameMode(entry.getGameMode())
			.member(member)
			.build();
	}
}
