package com.gnimty.communityapiserver.domain.member.repository;

import com.gnimty.communityapiserver.domain.introduction.entity.Introduction;
import com.gnimty.communityapiserver.domain.member.service.dto.response.OtherProfileServiceResponse;
import com.gnimty.communityapiserver.domain.member.service.dto.response.PreferGameModeEntry;
import com.gnimty.communityapiserver.domain.prefergamemode.entity.PreferGameMode;
import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleEntry;
import com.gnimty.communityapiserver.domain.schedule.entity.Schedule;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.gnimty.communityapiserver.domain.introduction.entity.QIntroduction.introduction;
import static com.gnimty.communityapiserver.domain.member.entity.QMember.member;
import static com.gnimty.communityapiserver.domain.prefergamemode.entity.QPreferGameMode.preferGameMode;
import static com.gnimty.communityapiserver.domain.schedule.entity.QSchedule.schedule;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Boolean existsByEmail(String email) {
        return queryFactory
            .selectOne()
            .from(member)
            .where(member.email.eq(email))
            .fetchFirst() != null;
    }

    public Boolean existsById(Long id) {
        return queryFactory
            .selectOne()
            .from(member)
            .where(member.id.eq(id))
            .fetchFirst() != null;
    }

    public OtherProfileServiceResponse findOtherById(Long id) {
        List<Schedule> schedules = queryFactory
            .selectFrom(schedule)
            .join(schedule.member, member).on(schedule.member.id.eq(member.id))
            .where(schedule.member.id.eq(id))
            .fetch();
        Optional<Introduction> mainIntroduction = Optional.ofNullable(queryFactory
            .selectFrom(introduction)
            .join(introduction.member)
            .where(introduction.member.id.eq(id), isMainIntroduction())
            .fetchFirst());
        List<PreferGameMode> preferGameModes = queryFactory
            .selectFrom(preferGameMode)
            .join(preferGameMode.member)
            .where(preferGameMode.member.id.eq(id))
            .fetch();

        return OtherProfileServiceResponse.builder()
            .schedules(schedules.stream()
                .map(ScheduleEntry::from)
                .toList())
            .mainIntroduction(mainIntroduction.map(Introduction::getContent).orElse(null))
            .preferGameModes(preferGameModes.stream()
                .map(PreferGameModeEntry::from)
                .toList())
            .build();
    }

    private BooleanExpression isMainIntroduction() {
        return introduction.isMain.eq(true);
    }
}
