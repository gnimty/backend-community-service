package com.gnimty.communityapiserver.domain.prefergamemode.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.GameMode;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "prefer_game_mode")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreferGameMode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prefer_game_mode_id", columnDefinition = "BIGINT", nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "game_mode", columnDefinition = "VARCHAR(20)")
    private GameMode gameMode;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", updatable = false)
    private Member member;

    @Builder
    public PreferGameMode(
        GameMode gameMode,
        Member member
    ) {
        this.gameMode = gameMode;
        this.member = member;
    }
}
