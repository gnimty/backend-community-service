package com.gnimty.communityapiserver.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gnimty.communityapiserver.global.constant.ResponseMessage;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.handler.GlobalExceptionHandler.CustomFieldError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ApiStatus status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public static CommonResponse<Void> success(ResponseMessage message, HttpStatus status) {
        return new CommonResponse<>(new ApiStatus(message.getMessage(), status.value()), null);
    }

    public static CommonResponse<Void> success() {
        return new CommonResponse<>(null, null);
    }

    public static <T> CommonResponse<T> success(T data) {
        return CommonResponse.<T>builder().data(data).build();
    }

    public static CommonResponse<Void> fail(ErrorCode errorCode) {
        return new CommonResponse<>(
            new ApiStatus(errorCode.getMessage(), errorCode.getStatus().value()), null);
    }

    public static CommonResponse<Void> fail(ErrorCode errorCode, String message) {
        return new CommonResponse<>(new ApiStatus(message, errorCode.getStatus().value()), null);
    }

    public static CommonResponse<Void> fail(ErrorCode errorCode, String message, String field) {
        return new CommonResponse<>(new ApiStatus(message, errorCode.getStatus().value(), field),
            null);
    }

    public static <T extends CustomFieldError> CommonResponse<T> fail(ErrorCode errorCode, T data) {
        return CommonResponse.<T>builder()
            .status(
                new ApiStatus(data.getMessage(), errorCode.getStatus().value(), data.getField()))
            .build();
    }

    @Data
    private static class ApiStatus {

        private String message;
        private int code;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String field;

        public ApiStatus(String message, int code) {
            this.message = message;
            this.code = code;
        }

        public ApiStatus(String message, int code, String field) {
            this.message = message;
            this.code = code;
            this.field = field;
        }
    }

}