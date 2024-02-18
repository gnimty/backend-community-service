package com.gnimty.communityapiserver.domain.oauthinfo.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.Provider;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(
    name = "oauth_info",
    indexes = {
        @Index(name = "email_provider_idx", columnList = "email, provider"),
        @Index(name = "member_id_provider_idx", columnList = "member_id, provider")
    }
)
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
