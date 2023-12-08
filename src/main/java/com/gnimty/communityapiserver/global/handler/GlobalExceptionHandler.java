package com.gnimty.communityapiserver.global.handler;

import static com.gnimty.communityapiserver.global.exception.ErrorCode.HEADER_NOT_FOUND;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.MEDIA_TYPE_NOT_SUPPORTED;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.METHOD_NOT_ALLOWED;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.MISSING_REQUEST_PARAMETER;
import static com.gnimty.communityapiserver.global.exception.ErrorCode.URL_NOT_FOUND;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.response.CommonResponse;
import java.util.Arrays;
import javax.validation.Path;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<CommonResponse<Void>> noHandlerFoundException(NoHandlerFoundException e) {

        ErrorCode errorCode = URL_NOT_FOUND;
        String message = String.format(URL_NOT_FOUND.getMessage(), e.getRequestURL());

        CommonResponse<Void> response = CommonResponse.fail(errorCode, message);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> methodNotAllowedException(
        HttpRequestMethodNotSupportedException e) {

        ErrorCode errorCode = METHOD_NOT_ALLOWED;
        String message = String.format(errorCode.getMessage(), e.getMethod(),
            Arrays.toString(e.getSupportedMethods()));

        CommonResponse<Void> response = CommonResponse.fail(errorCode, message);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<CommonResponse<Void>> httpMediaTypeNotSupportedException(
        HttpMediaTypeNotSupportedException e) {

        ErrorCode errorCode = MEDIA_TYPE_NOT_SUPPORTED;
        String message = String.format(errorCode.getMessage(), e.getContentType(),
            e.getSupportedMediaTypes());

        CommonResponse<Void> response = CommonResponse.fail(errorCode, message);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<CommonResponse<Void>> handleRuntimeException(BaseException e) {

        ErrorCode errorCode = e.getErrorCode();
        String message = e.getMessage();
        CommonResponse<Void> response = CommonResponse.fail(errorCode, message);

        return ResponseEntity.status(e.getErrorCode().getStatus()).body(response);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<CommonResponse<CustomFieldError>> BindExceptionHandler(BindException ex,
        BindingResult result) {
        if (result.getFieldErrors().isEmpty()) {
            ObjectError err = result.getAllErrors().stream().findFirst().get();
            return new ResponseEntity<>(CommonResponse.fail(
                ErrorCode.CONSTRAINT_VIOLATION,
                CustomFieldError.builder()
                    .message(err.getDefaultMessage())
                    .field(err.getObjectName())
                    .build()
            ), HttpStatus.BAD_REQUEST);
        }

        FieldError err = result.getFieldErrors().stream().findFirst().get();
        return new ResponseEntity<>(CommonResponse.fail(
            ErrorCode.CONSTRAINT_VIOLATION,
            CustomFieldError.builder()
                .message(ErrorMessage.INVALID_INPUT_VALUE)
                .field(err.getField())
                .build()
        ), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<CommonResponse<Void>> missingRequestHeaderExceptionHandler() {
        CommonResponse<Void> response = CommonResponse.fail(HEADER_NOT_FOUND);
        return ResponseEntity.status(HEADER_NOT_FOUND.getStatus()).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CommonResponse<Void>> missingRequestParameterExceptionHandler(
        MissingServletRequestParameterException ex) {
        String message = String.format(ErrorMessage.MISSING_REQUEST_PARAMETER,
            ex.getParameterName());
        ErrorCode errorCode = MISSING_REQUEST_PARAMETER;
        CommonResponse<Void> response = CommonResponse.fail(errorCode, message);

        return ResponseEntity.status(errorCode.getStatus()).body(response);
    }

    @Data
    public static class CustomFieldError {

        private String field;
        private String message;

        @Builder
        public CustomFieldError(Path propertyPath, String field, String message) {
            if (field == null) {
                this.field = Arrays.stream(propertyPath.toString().split("\\."))
                    .reduce((first, second) -> second).orElse("none");
            } else {
                this.field = field;
            }
            this.message = message;
        }
    }
}
