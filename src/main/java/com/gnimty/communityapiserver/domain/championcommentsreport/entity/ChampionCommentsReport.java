package com.gnimty.communityapiserver.domain.championcommentsreport.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.championcomments.entity.ChampionComments;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.ReportType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(
    name = "champion_comments_report",
    indexes = {
        @Index(name = "member_id_champion_comments_id_idx", columnList = "member_id, champion_comments_id")
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChampionCommentsReport extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "champion_comments_report_id", columnDefinition = "BIGINT", unique = true, nullable = false)
    private Long id;

    @NotNull
    @Column(name = "report_type", columnDefinition = "VARCHAR(100)")
    private ReportType reportType;

    @Column(name = "report_comment", columnDefinition = "VARCHAR(100)")
    private String reportComment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "champion_comments_id", nullable = false)
    private ChampionComments championComments;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public ChampionCommentsReport(
        ReportType reportType,
        String reportComment,
        ChampionComments championComments,
        Member member
    ) {
        this.reportType = reportType;
        this.reportComment = reportComment;
        this.championComments = championComments;
        this.member = member;
    }
}
