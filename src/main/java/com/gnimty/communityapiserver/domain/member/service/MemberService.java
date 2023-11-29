package com.gnimty.communityapiserver.domain.member.service;

import static com.gnimty.communityapiserver.global.constant.KeyPrefix.PASSWORD;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.REFRESH;
import static com.gnimty.communityapiserver.global.constant.KeyPrefix.UPDATE_PASSWORD;

import com.gnimty.communityapiserver.domain.block.repository.BlockRepository;
import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.introduction.service.IntroductionReadService;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.member.service.dto.request.IntroductionUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.OauthLoginServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordEmailVerifyServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordResetServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PasswordUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.PreferGameModeUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.SendEmailServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.request.StatusUpdateServiceRequest;
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
import com.gnimty.communityapiserver.global.constant.Auth;
import com.gnimty.communityapiserver.global.constant.KeyPrefix;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Provider;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.utils.RandomCodeGenerator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
	private final StringRedisTemplate redisTemplate;
	private final PasswordEncoder passwordEncoder;
	private final PreferGameModeRepository preferGameModeRepository;
	private final MemberReadService memberReadService;
	private final MemberLikeRepository memberLikeRepository;
	private final MemberLikeReadService memberLikeReadService;
	private final BlockRepository blockRepository;
	private final ScheduleRepository scheduleRepository;
	private final MailSenderUtil mailSenderUtil;

	public RiotAccount summonerAccountLink(OauthLoginServiceRequest request) {
		Member member = MemberThreadLocal.get();

		String puuid = "puuid";
		// String puuid = riotOauthUtil.getPuuid(request.getAuthCode());
		// get summoner info by puuid

		riotAccountReadService.throwIfExistsByPuuid(puuid);
		Boolean existsMain = riotAccountReadService.existsByMemberId(member);

		RiotAccount riotAccount = riotAccountRepository.save(RiotAccount.builder()
			.summonerName("summonerName")
			.isMain(!existsMain)
			.queue(Tier.bronze)
			.lp(100L)
			.division(100)
			.mmr(100L)
			.frequentLane1(Lane.TOP)
			.frequentLane2(Lane.BOTTOM)
			.frequentChampionId1(1L)
			.frequentChampionId2(1L)
			.frequentChampionId3(1L)
			.puuid(puuid)
			.member(member)
			.build()
		);

		if (!existsMain) {
			member.updateNickname("summonerName");
			member.updateRsoLinked(true);
		}

		return riotAccount;
	}

	public void oauthAdditionalLink(Provider provider, OauthLoginServiceRequest request) {
		final Member member = MemberThreadLocal.get();
		throwIfAlreadyLinkedProvider(member, provider);
		String userEmail;
		if (provider.equals(Provider.KAKAO)) {
			userEmail = kakaoOauthUtil.getKakaoUserEmail(request.getAuthCode());
		} else {
			userEmail = googleOauthUtil.getGoogleUserEmail(request.getAuthCode());
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

	public RiotAccount updateMyProfile(MyProfileUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}

		RiotAccount riotAccount = updateMainRiotAccount(request, member);
		updateStatus(request.getStatus(), member);
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

		String code = RandomCodeGenerator.generateCodeByLength(6);
		mailSenderUtil.sendEmail(Auth.EMAIL_SUBJECT.getContent(), member.getEmail(), code,
			"password-mail", "static/images/banner-urf.png");
		String key = getRedisKey(PASSWORD, member.getEmail());
		saveInRedis(key, code, Auth.EMAIL_CODE_EXPIRATION.getExpiration());
	}

	public PasswordEmailVerifyServiceResponse verifyEmailAuthCode(
		PasswordEmailVerifyServiceRequest request) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		String emailAuthKey = getRedisKey(PASSWORD, request.getEmail());
		String savedCode = valueOperations.get(emailAuthKey);

		if (!request.getCode().equals(savedCode)) {
			throw new BaseException(ErrorCode.INVALID_EMAIL_AUTH_CODE);
		}
		UUID uuid = UUID.randomUUID();
		String passwordKey = getRedisKey(UPDATE_PASSWORD, request.getEmail());
		saveInRedis(passwordKey, uuid.toString(), Auth.PASSWORD_EXPIRATION.getExpiration());
		redisTemplate.delete(emailAuthKey);
		return PasswordEmailVerifyServiceResponse.builder()
			.uuid(uuid.toString())
			.build();
	}

	public void resetPassword(PasswordResetServiceRequest request) {
		ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

		String uuid = valueOperations.get(getRedisKey(UPDATE_PASSWORD, request.getEmail()));
		if (!request.getUuid().equals(uuid)) {
			throw new BaseException(ErrorCode.INVALID_UUID);
		}
		Member member = memberReadService.findByEmailOrElseThrow(request.getEmail(),
			new BaseException(ErrorCode.NOT_LOGIN_BY_FORM));
		member.updatePassword(passwordEncoder.encode(request.getPassword()));

		redisTemplate.delete(getRedisKey(UPDATE_PASSWORD, request.getEmail()));
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

	public void updateStatus(StatusUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		updateStatus(request.getStatus(), member);
		memberRepository.save(member);
	}

	public void updateIntroduction(IntroductionUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		updateIntroductions(request.getIntroductions(), member);
	}

	public void updatePreferGameMode(PreferGameModeUpdateServiceRequest request) {
		Member member = MemberThreadLocal.get();
		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		preferGameModeRepository.deleteByMember(member);
		request.getPreferGameModes().stream()
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
		redisTemplate.delete(getRedisKey(REFRESH, String.valueOf(member.getId())));
	}

	public void withdrawal() {
		Member member = MemberThreadLocal.get();
		List<MemberLike> targets = memberLikeReadService.findBySourceMember(member);
		targets.forEach(
			memberLike -> memberReadService.findById(memberLike.getTargetMember().getId())
				.decreaseUpCount()
		);
		memberLikeRepository.deleteAllFromMember(member.getId());
		riotAccountRepository.deleteAllFromMember(member.getId());
		// 챔피언 운용법, 댓글 좋아요
		oauthInfoRepository.deleteAllFromMember(member.getId());
		blockRepository.deleteAllFromMember(member.getId());
		scheduleRepository.deleteAllFromMember(member.getId());
		preferGameModeRepository.deleteAllFromMember(member.getId());
		introductionRepository.deleteAllFromMember(member.getId());
		memberRepository.delete(member);
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
			introductionReadService.throwIfNotExistsOrExceedMain(member);
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
		if (updateSize + insertSize > 3) {
			throw new BaseException(ErrorCode.EXCEED_INTRODUCTION_COUNT);
		}
	}

	private void throwIfDuplicatedIsMain(List<IntroductionEntry> introductions) {
		long mainCount = introductions.stream()
			.filter(IntroductionEntry::getIsMain)
			.count();
		if (mainCount > 1) {
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

	private void updateStatus(Status status, Member member) {
		if (status != null) {
			member.updateStatus(status);
		}
	}

	private RiotAccount updateMainRiotAccount(MyProfileUpdateServiceRequest request,
		Member member) {
		if (request.getMainRiotAccountId() != null) {
			RiotAccount prevMainAccount = riotAccountReadService.findMainAccountByMember(
				member);
			RiotAccount postMainAccount = riotAccountReadService.findById(
				request.getMainRiotAccountId());
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

	private String getRedisKey(KeyPrefix prefix, String key) {
		return prefix.getPrefix() + key;
	}

	private void saveInRedis(String key, String value, long timeout) {
		ValueOperations<String, String> stringValueOperations = redisTemplate.opsForValue();

		stringValueOperations.set(key, value);
		redisTemplate.expire(
			key,
			timeout,
			TimeUnit.MILLISECONDS
		);
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
