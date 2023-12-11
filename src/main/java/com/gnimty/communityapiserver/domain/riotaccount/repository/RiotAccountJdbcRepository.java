package com.gnimty.communityapiserver.domain.riotaccount.repository;

import com.gnimty.communityapiserver.domain.member.controller.dto.request.SummonerUpdateEntry;
import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import java.sql.PreparedStatement;
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
				+ "set r.internalName=?, "
				+ "r.name=?, "
				+ "r.internalTagName=?, "
				+ "r.tagLine=?, "
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
				+ "r.queueFlex=?, "
				+ "r.lpFlex=?, "
				+ "r.divisionFlex=?, "
				+ "r.mmrFlex=?, "
				+ "r.frequentLane1Flex=?, "
				+ "r.frequentLane2Flex=?, "
				+ "r.frequentChampionId1Flex=?, "
				+ "r.frequentChampionId2Flex=?, "
				+ "r.frequentChampionId3Flex=? "
				+ "where r.puuid=?";

		jdbcTemplate.batchUpdate(
			sql,
			riotAccounts,
			riotAccounts.size(),
			(PreparedStatement ps, RiotAccount riotAccount) -> {
				int idx = riotAccounts.indexOf(riotAccount);
				ps.setString(1, summonerUpdates.get(idx).getInternalName());
				ps.setString(2, summonerUpdates.get(idx).getName());
				ps.setString(3, summonerUpdates.get(idx).getInternalTagName());
				ps.setString(4, summonerUpdates.get(idx).getTagLine());
				ps.setString(5, summonerUpdates.get(idx).getTier().name());
				ps.setInt(6, summonerUpdates.get(idx).getDivision());
				ps.setLong(7, summonerUpdates.get(idx).getLp());
				ps.setLong(8, summonerUpdates.get(idx).getMmr());
				ps.setString(9, summonerUpdates.get(idx).getMostLanes().get(0).name());
				ps.setString(10, summonerUpdates.get(idx).getMostLanes().get(1).name());
				ps.setLong(11, summonerUpdates.get(idx).getMostChampionIds().get(0));
				ps.setLong(12, summonerUpdates.get(idx).getMostChampionIds().get(1));
				ps.setLong(13, summonerUpdates.get(idx).getMostChampionIds().get(2));
				ps.setLong(14, summonerUpdates.get(idx).getIconId());
				ps.setString(15, summonerUpdates.get(idx).getTierFlex().name());
				ps.setLong(16, summonerUpdates.get(idx).getLpFlex());
				ps.setInt(17, summonerUpdates.get(idx).getDivisionFlex());
				ps.setLong(18, summonerUpdates.get(idx).getMmrFlex());
				ps.setString(19, summonerUpdates.get(idx).getMostLanesFlex().get(0).name());
				ps.setString(20, summonerUpdates.get(idx).getMostLanesFlex().get(1).name());
				ps.setLong(21, summonerUpdates.get(idx).getMostChampionIdsFlex().get(0));
				ps.setLong(22, summonerUpdates.get(idx).getMostChampionIdsFlex().get(1));
				ps.setLong(23, summonerUpdates.get(idx).getMostChampionIdsFlex().get(2));
				ps.setString(24, summonerUpdates.get(idx).getPuuid());
			}
		);
	}
}
