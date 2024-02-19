package com.gnimty.communityapiserver.domain.championcomments.service;

import static com.gnimty.communityapiserver.global.constant.Bound.CHILD_COMMENTS_DEPTH;
import static com.gnimty.communityapiserver.global.constant.Bound.INITIAL_COUNT;
import static com.gnimty.communityapiserver.global.constant.Bound.PARENT_COMMENTS_DEPTH;
import static com.gnimty.communityapiserver.global.constant.WebClientType.GNIMTY_VERSION_URI;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.COMMENTS_ID_AND_CHAMPION_ID_INVALID;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.NO_PERMISSION;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsServiceRequest;
import com.gnimty.communityapiserver.domain.championcomments.service.dto.request.ChampionCommentsUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.service.MemberReadService;
import com.gnimty.communityapiserver.global.auth.MemberThreadLocal;
import com.gnimty.communityapiserver.global.dto.webclient.VersionInfo;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.utils.WebClientUtil;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChampionCommentsService {

	private final ChampionCommentsReadService championCommentsReadService;
	private final MemberReadService memberReadService;
	private final ChampionCommentsRepository championCommentsRepository;

	public void addComments(Long championId, ChampionCommentsServiceRequest request) {
		Member member = MemberThreadLocal.get();

		if (!member.getRsoLinked()) {
			throw new BaseException(ErrorCode.NOT_LINKED_RSO);
		}
		// 해당하는 championId, opponentChampionId가 올바른지 validation
		throwIfNotFoundMentionedMember(request);
		VersionInfo versionInfo = getVersionInfo();
		ChampionComments parentComments = findParentComments(request);
		validateAddRequest(request, parentComments, versionInfo);
		championCommentsRepository.save(getChampionComments(championId, request, member, parentComments, versionInfo));
	}

	private void validateAddRequest(
		ChampionCommentsServiceRequest request,
		ChampionComments parentComments,
		VersionInfo versionInfo
	) {
		if (!isChildComments(request)) {
			return;
		}
		if (invalidChildComments(request)) {
			throw new BaseException(ErrorCode.INVALID_CHILD_COMMENTS);
		}
		if (isNotSameVersion(parentComments, versionInfo)) {
			throw new BaseException(ErrorCode.INVALID_VERSION);
		}
	}

	private boolean isNotSameVersion(ChampionComments parentComments, VersionInfo versionInfo) {
		return !parentComments.getVersion().equals(versionInfo.getData().getVersion());
	}

	private boolean isChildComments(ChampionCommentsServiceRequest request) {
		return request.getDepth() == CHILD_COMMENTS_DEPTH.getValue();
	}

	private boolean invalidChildComments(ChampionCommentsServiceRequest request) {
		return request.getCommentsType() != null || request.getLane() != null
			|| request.getOpponentChampionId() != null;
	}

	private ChampionComments getChampionComments(
		Long championId,
		ChampionCommentsServiceRequest request,
		Member member,
		ChampionComments parentComments,
		VersionInfo versionInfo
	) {
		return ChampionComments.builder()
			.lane(request.getLane())
			.championId(championId)
			.opponentChampionId(request.getOpponentChampionId())
			.depth(request.getDepth())
			.mentionedMemberId(request.getMentionedMemberId())
			.contents(request.getContents())
			.commentsType(request.getCommentsType())
			.upCount((long) INITIAL_COUNT.getValue())
			.downCount((long) INITIAL_COUNT.getValue())
			.version(versionInfo.getData().getVersion())
			.member(member)
			.parentChampionComments(parentComments)
			.build();
	}

	private ChampionComments findParentComments(ChampionCommentsServiceRequest request) {
		if (request.getParentChampionCommentsId() == null) {
			return null;
		}
		ChampionComments parentComments = championCommentsReadService.findById(request.getParentChampionCommentsId());
		if (parentComments.getDepth() != PARENT_COMMENTS_DEPTH.getValue()) {
			throw new BaseException(ErrorCode.PARENT_COMMENTS_DEPTH_MUST_BE_ONE);
		}
		return parentComments;
	}

	private void throwIfNotFoundMentionedMember(ChampionCommentsServiceRequest request) {
		if (request.getMentionedMemberId() != null && !memberReadService.existsById(request.getMentionedMemberId())) {
			throw new BaseException(ErrorCode.MEMBER_NOT_FOUND);
		}
	}

	private VersionInfo getVersionInfo() {
		return WebClientUtil.get(VersionInfo.class, GNIMTY_VERSION_URI.getValue(), null);
	}

	public void updateComments(
		Long championId,
		Long commentsId,
		ChampionCommentsUpdateServiceRequest request
	) {
		Member member = MemberThreadLocal.get();
		ChampionComments championComments = championCommentsReadService.findById(commentsId);
		if (!Objects.equals(championComments.getMember().getId(), member.getId())) {
			throw new BaseException(NO_PERMISSION);
		}
		if (!Objects.equals(championComments.getChampionId(), championId)) {
			throw new BaseException(COMMENTS_ID_AND_CHAMPION_ID_INVALID);
		}

		championComments.updateMentionedMemberId(request.getMentionedMemberId());
		championComments.updateContents(request.getContents());
	}

	public void deleteComments(Long championId, Long commentsId) {
		Member member = MemberThreadLocal.get();
		ChampionComments championComments = championCommentsReadService.findById(commentsId);
		if (!Objects.equals(championComments.getMember().getId(), member.getId())) {
			throw new BaseException(NO_PERMISSION);
		}
		if (!Objects.equals(championComments.getChampionId(), championId)) {
			throw new BaseException(COMMENTS_ID_AND_CHAMPION_ID_INVALID);
		}
		championComments.delete();
	}
}
