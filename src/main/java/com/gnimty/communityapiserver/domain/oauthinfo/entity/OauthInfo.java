package com.gnimty.communityapiserver.domain.oauthinfo.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.Provider;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oauth_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_info_id", columnDefinition = "BIGINT", updatable = false, unique = true)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", columnDefinition = "VARCHAR(10)")
    private Provider provider;

    @Column(name = "email", columnDefinition = "VARCHAR(100)")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public OauthInfo(
        Provider provider,
        String email,
        Member member
    ) {
        this.provider = provider;
        this.email = email;
        this.member = member;
    }
}
