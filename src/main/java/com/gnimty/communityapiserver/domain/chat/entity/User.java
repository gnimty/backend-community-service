package com.gnimty.communityapiserver.domain.chat.entity;


import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;

import java.util.Optional;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Builder
@Slf4j
public class User {

    @Id
    private String id;
    @Indexed(unique = true)
    private Long actualUserId;

    private String name;
    private String tagLine;
    private String internalTagName;
    private Status nowStatus;
    private Status selectedStatus;
    private Long profileIconId;
    private String puuid;

    // 솔로 랭크
    private Tier tier;
    private Integer division;
    private Long lp;
    private Long mmr;
    private Lane frequentLane1;
    private Lane frequentLane2;
    private Long frequentChampionId1;
    private Long frequentChampionId2;
    private Long frequentChampionId3;

    // 자유 랭크
    private Tier tierFlex;
    private Integer divisionFlex;
    private Long lpFlex;
    private Long mmrFlex;
    private Lane frequentLane1Flex;
    private Lane frequentLane2Flex;
    private Long frequentChampionId1Flex;
    private Long frequentChampionId2Flex;
    private Long frequentChampionId3Flex;


    public static User toUser(RiotAccount riotAccount) {
        return User.builder()
                .actualUserId(riotAccount.getMember().getId())
                .name(riotAccount.getName())
                .tagLine(riotAccount.getTagLine())
                .internalTagName(riotAccount.getInternalTagName())
                .profileIconId(riotAccount.getIconId())
                .puuid(riotAccount.getPuuid())
                .selectedStatus(Status.ONLINE)
                // 솔로 랭크
                .tier(riotAccount.getQueue())
                .division(riotAccount.getDivision())
                .lp(riotAccount.getLp())
                .mmr(riotAccount.getMmr())
                .frequentLane1(riotAccount.getFrequentLane1())
                .frequentLane2(riotAccount.getFrequentLane2())
                .frequentChampionId1(riotAccount.getFrequentChampionId1())
                .frequentChampionId2(riotAccount.getFrequentChampionId2())
                .frequentChampionId3(riotAccount.getFrequentChampionId3())
                // 자유 랭크
                .tierFlex(riotAccount.getQueueFlex())
                .divisionFlex(riotAccount.getDivisionFlex())
                .lpFlex(riotAccount.getLpFlex())
                .mmrFlex(riotAccount.getMmrFlex())
                .frequentLane1Flex(riotAccount.getFrequentLane1Flex())
                .frequentLane2Flex(riotAccount.getFrequentLane2Flex())
                .frequentChampionId1Flex(riotAccount.getFrequentChampionId1Flex())
                .frequentChampionId2Flex(riotAccount.getFrequentChampionId2Flex())
                .frequentChampionId3Flex(riotAccount.getFrequentChampionId3Flex())
                .build();
    }

    public void updateByRiotAccount(RiotAccount riotAccount) {
        this.actualUserId = Optional.ofNullable(riotAccount.getMember().getId()).orElse(this.actualUserId);
        this.name = Optional.ofNullable(riotAccount.getName()).orElse(this.name);
        this.tagLine = Optional.ofNullable(riotAccount.getTagLine()).orElse(this.tagLine);
        this.internalTagName = Optional.ofNullable(riotAccount.getInternalTagName()).orElse(this.internalTagName);
        this.profileIconId = Optional.ofNullable(riotAccount.getIconId()).orElse(this.profileIconId);
        this.puuid = Optional.ofNullable(riotAccount.getPuuid()).orElse(this.puuid);
        // 솔로 랭크
        this.tier = Optional.ofNullable(riotAccount.getQueue()).orElse(this.tier);
        this.division = Optional.ofNullable(riotAccount.getDivision()).orElse(this.division);
        this.lp = Optional.ofNullable(riotAccount.getLp()).orElse(this.lp);
        this.mmr = Optional.ofNullable(riotAccount.getMmr()).orElse(this.mmr);
        this.frequentLane1 = Optional.ofNullable(riotAccount.getFrequentLane1()).orElse(this.frequentLane1);
        this.frequentLane2 = Optional.ofNullable(riotAccount.getFrequentLane2()).orElse(this.frequentLane2);
        this.frequentChampionId1 = Optional.ofNullable(riotAccount.getFrequentChampionId1())
                .orElse(this.frequentChampionId1);
        this.frequentChampionId2 = Optional.ofNullable(riotAccount.getFrequentChampionId2())
                .orElse(this.frequentChampionId2);
        this.frequentChampionId3 = Optional.ofNullable(riotAccount.getFrequentChampionId3())
                .orElse(this.frequentChampionId3);
        // 자유 랭크
        this.tierFlex = Optional.ofNullable(riotAccount.getQueueFlex()).orElse(this.tierFlex);
        this.divisionFlex = Optional.ofNullable(riotAccount.getDivisionFlex()).orElse(this.divisionFlex);
        this.lpFlex = Optional.ofNullable(riotAccount.getLpFlex()).orElse(this.lpFlex);
        this.mmrFlex = Optional.ofNullable(riotAccount.getMmrFlex()).orElse(this.mmrFlex);
        this.frequentLane1Flex = Optional.ofNullable(riotAccount.getFrequentLane1Flex()).orElse(this.frequentLane1Flex);
        this.frequentLane2Flex = Optional.ofNullable(riotAccount.getFrequentLane2Flex()).orElse(this.frequentLane2Flex);
        this.frequentChampionId1Flex = Optional.ofNullable(riotAccount.getFrequentChampionId1Flex())
                .orElse(this.frequentChampionId1Flex);
        this.frequentChampionId2Flex = Optional.ofNullable(riotAccount.getFrequentChampionId2Flex())
                .orElse(this.frequentChampionId2Flex);
        this.frequentChampionId3Flex = Optional.ofNullable(riotAccount.getFrequentChampionId3Flex())
                .orElse(this.frequentChampionId3Flex);
    }

    public void updateNowStatus(Status nowStatus) {
        this.nowStatus = nowStatus;
    }

    public void updateSelectedStatus(Status selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

}