package com.gnimty.communityapiserver.domain.member.controller.dto.request;

import com.gnimty.communityapiserver.domain.member.service.dto.request.MyProfileUpdateServiceRequest;
import com.gnimty.communityapiserver.domain.member.service.dto.response.IntroductionEntry;
import com.gnimty.communityapiserver.global.constant.Status;
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
public class MyProfileUpdateRequest {

    private Long mainRiotAccountId;
    private Status status;
    @Valid
    private List<IntroductionEntry> introductions;

    public MyProfileUpdateServiceRequest toServiceRequest() {
        return MyProfileUpdateServiceRequest.builder()
            .mainRiotAccountId(mainRiotAccountId)
            .status(status)
            .introductions(introductions)
            .build();
    }
}
