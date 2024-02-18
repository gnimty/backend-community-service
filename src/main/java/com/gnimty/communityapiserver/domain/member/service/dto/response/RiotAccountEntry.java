package com.gnimty.communityapiserver.domain.member.service.dto.response;

import com.gnimty.communityapiserver.domain.riotaccount.entity.RiotAccount;
import com.gnimty.communityapiserver.global.constant.Lane;
import com.gnimty.communityapiserver.global.constant.Tier;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RiotAccountEntry {

    @Schema(example = "1", description = "소환사 id")
    private Long id;
    @Schema(example = "name", description = "소환사 name")
    private String name;
    @Schema(example = "tag", description = "소환사 tag")
    private String tagLine;
    @Schema(example = "true", description = "메인 소환사 여부")
    private Boolean isMain;
    @Schema(example = "puuid", description = "소환사 puuid")
    private String puuid;
    @Schema(example = "DIAMOND", description = "솔로랭크 티어")
    private Tier queue;
    @Schema(example = "100", description = "솔로랭크 lp")
    private Long lp;
    @Schema(example = "1", description = "솔로랭크 세부 티어")
    private Integer division;
    @Schema(example = "1000", description = "솔로랭크 mmr")
    private Long mmr;
    @Schema(example = "TOP", description = "솔로랭크 자주 가는 라인 1")
    private Lane frequentLane1;
    @Schema(example = "MIDDLE", description = "솔로랭크 자주 가는 라인 2")
    private Lane frequentLane2;
    @Schema(example = "1", description = "솔로랭크 자주 플레이하는 챔피언 id 1")
    private Long frequentChampionId1;
    @Schema(example = "2", description = "솔로랭크 자주 플레이하는 챔피언 id 2")
    private Long frequentChampionId2;
    @Schema(example = "3", description = "솔로랭크 자주 플레이하는 챔피언 id 3")
    private Long frequentChampionId3;
    @Schema(example = "DIAMOND", description = "자유랭크 티어")
    private Tier queueFlex;
    @Schema(example = "100", description = "자유랭크 lp")
    private Long lpFlex;
    @Schema(example = "1", description = "자유랭크 세부 티어")
    private Integer divisionFlex;
    @Schema(example = "1000", description = "자유랭크 mmr")
    private Long mmrFlex;
    @Schema(example = "TOP", description = "자유랭크 자주 가는 라인 1")
    private Lane frequentLane1Flex;
    @Schema(example = "MIDDLE", description = "자유랭크 자주 가는 라인 2")
    private Lane frequentLane2Flex;
    @Schema(example = "1", description = "자유랭크 자주 플레이하는 챔피언 id 1")
    private Long frequentChampionId1Flex;
    @Schema(example = "2", description = "자유랭크 자주 플레이하는 챔피언 id 2")
    private Long frequentChampionId2Flex;
    @Schema(example = "3", description = "자유랭크 자주 플레이하는 챔피언 id 3")
    private Long frequentChampionId3Flex;
    @Schema(example = "1", description = "소환사 아이콘 id")
    private Long iconId;

    public static RiotAccountEntry from(RiotAccount riotAccount) {
        return RiotAccountEntry.builder()
            .id(riotAccount.getId())
            .name(riotAccount.getName())
            .tagLine(riotAccount.getTagLine())
            .isMain(riotAccount.getIsMain())
            .puuid(riotAccount.getPuuid())
            .queue(riotAccount.getQueue())
            .lp(riotAccount.getLp())
            .division(riotAccount.getDivision())
            .mmr(riotAccount.getMmr())
            .frequentLane1(riotAccount.getFrequentLane1())
            .frequentLane2(riotAccount.getFrequentLane2())
            .frequentChampionId1(riotAccount.getFrequentChampionId1())
            .frequentChampionId2(riotAccount.getFrequentChampionId2())
            .frequentChampionId3(riotAccount.getFrequentChampionId3())
            .queueFlex(riotAccount.getQueueFlex())
            .lpFlex(riotAccount.getLpFlex())
            .divisionFlex(riotAccount.getDivisionFlex())
            .mmrFlex(riotAccount.getMmrFlex())
            .frequentLane1Flex(riotAccount.getFrequentLane1Flex())
            .frequentLane2Flex(riotAccount.getFrequentLane2Flex())
            .frequentChampionId1Flex(riotAccount.getFrequentChampionId1Flex())
            .frequentChampionId2Flex(riotAccount.getFrequentChampionId2Flex())
            .frequentChampionId3Flex(riotAccount.getFrequentChampionId3Flex())
            .iconId(riotAccount.getIconId())
            .build();
    }
}
