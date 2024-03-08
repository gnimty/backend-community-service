package com.gnimty.communityapiserver.domain.member.service;

import com.gnimty.communityapiserver.domain.block.repository.BlockRepository;
import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.championcomments.repository.ChampionCommentsRepository;
import com.gnimty.communityapiserver.domain.championcommentslike.repository.ChampionCommentsLikeRepository;
import com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry.ChampionCommentsReportRepository;
import com.gnimty.communityapiserver.domain.introduction.repository.IntroductionRepository;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.domain.member.repository.MemberRepository;
import com.gnimty.communityapiserver.domain.memberlike.repository.MemberLikeRepository;
import com.gnimty.communityapiserver.domain.oauthinfo.repository.OauthInfoRepository;
import com.gnimty.communityapiserver.domain.prefergamemode.repository.PreferGameModeRepository;
import com.gnimty.communityapiserver.domain.riotaccount.repository.RiotAccountRepository;
import com.gnimty.communityapiserver.domain.schedule.repository.ScheduleRepository;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WithdrawalService {

	private static final int MAXIMUM_RETRY = 10;
	private static final String WITHDRAWAL_SCHEDULER_CRON_EXPRESSION = "0 0 5 * * *";
	private static final String[] QUERY_PARAMS = {
		"introduction",
		"prefer_game_mode",
		"schedule",
		"block",
		"oauth_info",
		"member_like",
		"riot_account",
		"champion_comments_like",
		"champion_comments_report",
		"member"
	};
	private static final String UPDATE_MEMBER_SQL = """
			update member m
			set m.up_count = m.up_count - 1, m.lockVersion = m.lockVersion + 1, m.updated_at = ?
			where m.member_id = ?
			and m.lockVersion = ?
		""";
	private static final String UPDATE_CHAMPION_COMMENTS_LIKE_SQL = """
			update champion_comments c
			set c.up_count = c.up_count - 1, c.lockVersion = c.lockVersion + 1, c.updated_at = ?
			where c.champion_comments_id = ?
			and c.lockVersion = ?
		""";
	private static final String UPDATE_CHAMPION_COMMENTS_DISLIKE_SQL = """
			update champion_comments c
			set c.down_count = c.down_count - 1, c.lockVersion = c.lockVersion + 1, c.updated_at = ?
			where c.champion_comments_id = ?
			and c.lockVersion = ?
		""";

	private final IntroductionRepository introductionRepository;
	private final PreferGameModeRepository preferGameModeRepository;
	private final ScheduleRepository scheduleRepository;
	private final BlockRepository blockRepository;
	private final OauthInfoRepository oauthInfoRepository;
	private final MemberLikeRepository memberLikeRepository;
	private final RiotAccountRepository riotAccountRepository;
	private final ChampionCommentsLikeRepository championCommentsLikeRepository;
	private final ChampionCommentsReportRepository championCommentsReportRepository;
	private final ChampionCommentsRepository championCommentsRepository;
	private final MemberRepository memberRepository;
	private final JdbcTemplate jdbcTemplate;
	private final EntityManager entityManager;

	public void withdrawal(Long id) {
		LocalDateTime now = LocalDateTime.now();
		introductionRepository.deleteAllFromMember(id, now);
		preferGameModeRepository.deleteAllFromMember(id, now);
		scheduleRepository.deleteAllFromMember(id, now);
		blockRepository.deleteAllFromMember(id, now);
		oauthInfoRepository.deleteAllFromMember(id, now);
		memberLikeDelete(id, now);
		riotAccountRepository.deleteAllFromMember(id, now);
		championCommentsLikeDelete(id, now);
		championCommentsReportRepository.deleteAllFromMember(id, now);
		championCommentsRepository.deleteAllFromMember(id, now);
		memberRepository.deleteAllFromMember(id, now);
	}

	private void memberLikeDelete(Long id, LocalDateTime now) {
		List<Long> targetIds = memberLikeRepository.findTargetIdsBySourceMemberId(id);
		targetIds.forEach(targetId -> {
			int rowsUpdated = 0;
			int retryCount = 0;
			while (rowsUpdated != 1 && retryCount < MAXIMUM_RETRY) {
				Optional<Member> optionalMember = memberRepository.findById(targetId);
				if (optionalMember.isEmpty()) {
					return;
				}
				Member updateMember = optionalMember.get();
				rowsUpdated = jdbcTemplate.update(UPDATE_MEMBER_SQL, ps -> {
					ps.setTimestamp(1, Timestamp.valueOf(now));
					ps.setLong(2, updateMember.getId());
					ps.setLong(3, updateMember.getLockVersion());
				});
				retryCount++;
			}
			entityManager.flush();
			entityManager.clear();
		});
		memberLikeRepository.deleteAllFromMember(id);
	}

	private void championCommentsLikeDelete(Long id, LocalDateTime now) {
		List<Long> championCommentsLikeIds = championCommentsLikeRepository.findByMemberIdAndLikeOrNot(id, true);
		List<Long> championCommentsDislikeIds = championCommentsLikeRepository.findByMemberIdAndLikeOrNot(id, false);

		updateChampionCommentsReaction(now, championCommentsLikeIds, UPDATE_CHAMPION_COMMENTS_LIKE_SQL);
		updateChampionCommentsReaction(now, championCommentsDislikeIds, UPDATE_CHAMPION_COMMENTS_DISLIKE_SQL);
		championCommentsLikeRepository.deleteAllFromMember(id);
	}

	private void updateChampionCommentsReaction(LocalDateTime now, List<Long> championCommentsLikeIds,
		String likeUpdateSql) {
		championCommentsLikeIds.forEach(likeId -> {
			int rowsUpdated = 0;
			int retryCount = 0;
			while (rowsUpdated != 1 && retryCount < MAXIMUM_RETRY) {
				Optional<ChampionComments> optionalChampionComments = championCommentsRepository.findById(likeId);
				if (optionalChampionComments.isEmpty()) {
					return;
				}
				ChampionComments championComments = optionalChampionComments.get();
				rowsUpdated = jdbcTemplate.update(likeUpdateSql, ps -> {
					ps.setTimestamp(1, Timestamp.valueOf(now));
					ps.setLong(2, championComments.getId());
					ps.setLong(3, championComments.getLockVersion());
				});
				retryCount++;
			}
			entityManager.flush();
			entityManager.clear();
		});
	}

	@Scheduled(cron = WITHDRAWAL_SCHEDULER_CRON_EXPRESSION)
	public void physicalDeleteDatePassed() {
		Arrays.stream(QUERY_PARAMS).forEach(this::deleteWhereDeletedAndTimeElapsed);
	}

	private void deleteWhereDeletedAndTimeElapsed(String entity) {
		LocalDateTime date = LocalDate.now().atStartOfDay().minusDays(3);
		String sql = "DELETE FROM " + entity + " e WHERE e.deleted = 1 AND e.updated_at < ?";
		int rowsUpdated = 0;
		int retryCount = 0;
		while (rowsUpdated != 1 && retryCount < MAXIMUM_RETRY) {
			rowsUpdated = jdbcTemplate.update(sql, ps -> ps.setTimestamp(1, Timestamp.valueOf(date)));
			retryCount++;
		}
	}
}
