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

	public void processBatchUpdate(List<RiotAccount> riotAccounts,
		List<SummonerUpdateEntry> summonerUpdates) {
		String sql =
			"update riot_account r "
				+ "set r.summoner_name=?, "
				+ "r.queue=?, "
				+ "r.division=?, "
				+ "r.lp=?, "
				+ "r.mmr=?, "
				+ "r.frequent_lane_1=?, "
				+ "r.frequent_lane_2=?, "
				+ "r.frequent_champion_id_1=?, "
				+ "r.frequent_champion_id_2=?, "
				+ "r.frequent_champion_id_3=?, "
				+ "r.icon_id=? "
				+ "where r.puuid=?";

		jdbcTemplate.batchUpdate(
			sql,
			riotAccounts,
			riotAccounts.size(),
			(PreparedStatement ps, RiotAccount riotAccount) -> {
				int idx = riotAccounts.indexOf(riotAccount);
				ps.setString(1, summonerUpdates.get(idx).getSummonerName());
				ps.setString(2, summonerUpdates.get(idx).getTier().name());
				ps.setInt(3, summonerUpdates.get(idx).getDivision());
				ps.setLong(4, summonerUpdates.get(idx).getLp());
				ps.setLong(5, summonerUpdates.get(idx).getMmr());
				ps.setString(6, summonerUpdates.get(idx).getMostLanes().get(0).name());
				ps.setString(7, summonerUpdates.get(idx).getMostLanes().get(1).name());
				ps.setLong(8, summonerUpdates.get(idx).getMostChampionIds().get(0));
				ps.setLong(9, summonerUpdates.get(idx).getMostChampionIds().get(1));
				ps.setLong(10, summonerUpdates.get(idx).getMostChampionIds().get(2));
				ps.setLong(11, summonerUpdates.get(idx).getIconId());
				ps.setString(12, summonerUpdates.get(idx).getPuuid());
			}
		);
	}
}
