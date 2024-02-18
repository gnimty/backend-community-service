package com.gnimty.communityapiserver.domain.member.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.global.constant.Status;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
    name = "member",
    indexes = {
        @Index(name = "email_idx", columnList = "email")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", columnDefinition = "BIGINT", updatable = false, unique = true)
    private Long id;

    @Column(name = "rso_linked", columnDefinition = "TINYINT")
    @NotNull
    private Boolean rsoLinked;

    @Column(name = "email", columnDefinition = "VARCHAR(100)", unique = true)
    private String email;

    @Column(name = "password", columnDefinition = "VARCHAR(100)")
    private String password;

    @Column(name = "favorite_champion_id", columnDefinition = "BIGINT")
    private Long favoriteChampionID;

    @Column(name = "nickname", columnDefinition = "VARCHAR(16)", unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(10)")
    private Status status;

    @Column(name = "up_count", columnDefinition = "BIGINT")
    @NotNull
    private Long upCount;

    @Builder
    public Member(
        Boolean rsoLinked,
        String email,
        String password,
        Long favoriteChampionID,
        String nickname,
        Status status,
        Long upCount
    ) {
        this.rsoLinked = rsoLinked;
        this.email = email;
        this.password = password;
        this.favoriteChampionID = favoriteChampionID;
        this.nickname = nickname;
        this.status = status;
        this.upCount = upCount;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateRsoLinked(Boolean rsoLinked) {
        this.rsoLinked = rsoLinked;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void decreaseUpCount() {
        this.upCount--;
    }

    public void increaseUpCount() {
        this.upCount++;
    }
}
