package com.gnimty.communityapiserver.domain.schedule.controller;

import static org.springframework.http.HttpStatus.OK;

import com.gnimty.communityapiserver.domain.schedule.controller.dto.request.ScheduleRequest;
import com.gnimty.communityapiserver.domain.schedule.service.ScheduleService;
import com.gnimty.communityapiserver.global.constant.ResponseMessage;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/me/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PatchMapping
    public CommonResponse<Void> updateSchedules(@RequestBody @Valid ScheduleRequest request) {
        scheduleService.updateSchedules(request.toServiceRequest());
        return CommonResponse.success(ResponseMessage.SUCCESS_UPDATE_SCHEDULES, OK);
    }
}
