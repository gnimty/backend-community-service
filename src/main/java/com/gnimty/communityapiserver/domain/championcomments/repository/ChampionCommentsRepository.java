package com.gnimty.communityapiserver.domain.championcomments.repository;

import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionCommentsRepository extends JpaRepository<ChampionComments, Long> {

}
