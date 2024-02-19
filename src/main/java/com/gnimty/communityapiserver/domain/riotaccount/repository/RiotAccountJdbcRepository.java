package com.gnimty.communityapiserver.domain.riotaccount.repository;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
@Transactional
public class RiotAccountJdbcRepository {

	private final JdbcTemplate jdbcTemplate;

	public void processBatchUpdate(List<RiotAccount> riotAccounts, List<SummonerUpdateEntry> summonerUpdates) {
		String sql =
			"update riot_account r "
				+ "set r.name=?, "
				+ "r.internal_tag_name=?, "
				+ "r.tag_line=?, "
				+ "r.queue=?, "
				+ "r.division=?, "
				+ "r.lp=?, "
				+ "r.mmr=?, "
				+ "r.frequent_lane_1=?, "
				+ "r.frequent_lane_2=?, "
				+ "r.frequent_champion_id_1=?, "
				+ "r.frequent_champion_id_2=?, "
				+ "r.frequent_champion_id_3=?, "
				+ "r.icon_id=?, "
				+ "r.queue_flex=?, "
				+ "r.lp_flex=?, "
				+ "r.division_flex=?, "
				+ "r.mmr_flex=?, "
				+ "r.frequent_lane_1_flex=?, "
				+ "r.frequent_lane_2_flex=?, "
				+ "r.frequent_champion_id_1_flex=?, "
				+ "r.frequent_champion_id_2_flex=?, "
				+ "r.frequent_champion_id_3_flex=?, "
				+ "r.updated_at=? "
				+ "where r.puuid=?";

		jdbcTemplate.batchUpdate(
			sql,
			riotAccounts,
			riotAccounts.size(),
			(PreparedStatement ps, RiotAccount riotAccount) -> {
				int idx = riotAccounts.indexOf(riotAccount);
				ps.setString(1, summonerUpdates.get(idx).getName());
				ps.setString(2, summonerUpdates.get(idx).getInternalTagName());
				ps.setString(3, summonerUpdates.get(idx).getTagLine());
				ps.setObject(4, summonerUpdates.get(idx).getTier() == null ?
					null : summonerUpdates.get(idx).getTier().name(), Types.VARCHAR);
				ps.setObject(5, summonerUpdates.get(idx).getDivision(), Types.TINYINT);
				ps.setObject(6, summonerUpdates.get(idx).getLp(), Types.BIGINT);
				ps.setObject(7, summonerUpdates.get(idx).getMmr(), Types.BIGINT);
				ps.setObject(8, safeGetLane(summonerUpdates.get(idx).getMostLanes(), 0), Types.VARCHAR);
				ps.setObject(9, safeGetLane(summonerUpdates.get(idx).getMostLanes(), 1), Types.VARCHAR);
				ps.setObject(10, safeGetId(summonerUpdates.get(idx).getMostChampionIds(), 0), Types.BIGINT);
				ps.setObject(11, safeGetId(summonerUpdates.get(idx).getMostChampionIds(), 1), Types.BIGINT);
				ps.setObject(12, safeGetId(summonerUpdates.get(idx).getMostChampionIds(), 2), Types.BIGINT);
				ps.setLong(13, summonerUpdates.get(idx).getIconId());
				ps.setObject(14, summonerUpdates.get(idx).getTierFlex() == null ?
					null : summonerUpdates.get(idx).getTierFlex().name(), Types.VARCHAR);
				ps.setObject(15, summonerUpdates.get(idx).getLpFlex(), Types.BIGINT);
				ps.setObject(16, summonerUpdates.get(idx).getDivisionFlex(), Types.TINYINT);
				ps.setObject(17, summonerUpdates.get(idx).getMmrFlex(), Types.BIGINT);
				ps.setObject(18, safeGetLane(summonerUpdates.get(idx).getMostLanesFlex(), 0), Types.VARCHAR);
				ps.setObject(19, safeGetLane(summonerUpdates.get(idx).getMostLanesFlex(), 1), Types.VARCHAR);
				ps.setObject(20, safeGetId(summonerUpdates.get(idx).getMostChampionIdsFlex(), 0), Types.BIGINT);
				ps.setObject(21, safeGetId(summonerUpdates.get(idx).getMostChampionIdsFlex(), 1), Types.BIGINT);
				ps.setObject(22, safeGetId(summonerUpdates.get(idx).getMostChampionIdsFlex(), 2), Types.BIGINT);
				ps.setTimestamp(23, Timestamp.valueOf(LocalDateTime.now()));
				ps.setString(24, summonerUpdates.get(idx).getPuuid());
			}
		);
	}

	private String safeGetLane(List<Lane> lanes, int index) {
		if (index >= 0 && index < lanes.size()) {
			return lanes.get(index).name();
		}
		return null;
	}

	private Long safeGetId(List<Long> championIds, int index) {
		if (index >= 0 && index < championIds.size()) {
			return championIds.get(index);
		}
		return null;
	}
}
