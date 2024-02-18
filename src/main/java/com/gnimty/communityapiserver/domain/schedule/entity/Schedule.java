package com.gnimty.communityapiserver.domain.schedule.entity;

import com.gnimty.communityapiserver.domain.base.entity.BaseEntity;
import com.gnimty.communityapiserver.domain.member.entity.Member;
import com.gnimty.communityapiserver.global.constant.DayOfWeek;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id", columnDefinition = "BIGINT", updatable = false, nullable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", columnDefinition = "VARCHAR(20)")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", columnDefinition = "INTEGER")
    private Integer startTime;

    @Column(name = "end_time", columnDefinition = "INTEGER")
    private Integer endTime;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Schedule(
        DayOfWeek dayOfWeek,
        Integer startTime,
        Integer endTime,
        Member member
    ) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.member = member;
    }
}
