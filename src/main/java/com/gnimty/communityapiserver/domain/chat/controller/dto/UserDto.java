package com.gnimty.communityapiserver.domain.chat.controller.dto;

import com.gnimty.communityapiserver.domain.chat.entity.User;
import com.gnimty.communityapiserver.global.constant.Status;
import com.gnimty.communityapiserver.global.constant.Tier;
import lombok.Data;


@Data
public class UserDto {

    private Long userId;
    private String summonerName;
    private Tier tier;
    private Integer division;
    private Long iconId;
    private Status status;

    public UserDto(User user) {
        this.userId = user.getActualUserId();
        this.status = user.getStatus();
        this.tier = user.getTier();
        this.division = user.getDivision();
        this.iconId = user.getProfileIconId();
        this.summonerName = user.getSummonerName();
    }

}
