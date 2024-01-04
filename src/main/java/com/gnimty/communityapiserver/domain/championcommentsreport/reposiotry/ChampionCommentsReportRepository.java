package com.gnimty.communityapiserver.domain.championcommentsreport.reposiotry;

import com.gnimty.communityapiserver.domain.championcommentsreport.entity.ChampionCommentsReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionCommentsReportRepository extends JpaRepository<ChampionCommentsReport, Long> {

}
