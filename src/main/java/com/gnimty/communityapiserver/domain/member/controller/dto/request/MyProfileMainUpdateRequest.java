package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateMainServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.global.constant.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileMainUpdateRequest {

	@Schema(example = "변경할 메인 소환사 id")
	private Long mainRiotAccountId;
	@Schema(example = "변경할 상태")
	private Status status;
	@Builder.Default
	@Schema(example = "변경할 소개글 정보")
	private List<@Valid IntroductionEntry> introductions = new ArrayList<>();

	public MyProfileUpdateMainServiceRequest toServiceRequest() {
		return MyProfileUpdateMainServiceRequest.builder()
			.mainRiotAccountId(mainRiotAccountId)
			.status(status)
			.introductions(introductions)
			.build();
	}
}
