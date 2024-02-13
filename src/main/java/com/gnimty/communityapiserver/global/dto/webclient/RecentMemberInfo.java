package com.gnimty.communityapiserver.global.dto.webclient;

import java.util.List;
import lombok.Getter;

@Getter
public class RecentMemberInfo {

	private Integer count;
	private List<RecentMemberDto> recentMembers;
}