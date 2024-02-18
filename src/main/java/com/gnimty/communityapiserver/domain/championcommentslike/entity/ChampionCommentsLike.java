package com.gnimty.communityapiserver.domain.championcommentslike.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "champion_comments_like",
    indexes = {
        @Index(name = "member_id_champion_comments_id_idx", columnList = "member_id, champion_comments_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChampionCommentsLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "champion_comments_like_id", columnDefinition = "BIGINT", unique = true, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "like_or_not", columnDefinition = "TINYINT")
    private Boolean likeOrNot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "champion_comments_id", nullable = false)
    private ChampionComments championComments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public ChampionCommentsLike(
        Boolean likeOrNot,
        ChampionComments championComments,
        Member member
    ) {
        this.likeOrNot = likeOrNot;
        this.championComments = championComments;
        this.member = member;
    }
}
