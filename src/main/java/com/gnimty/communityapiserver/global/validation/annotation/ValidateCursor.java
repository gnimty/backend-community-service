package com.gnimty.communityapiserver.global.validation.annotation;

import com.gnimty.communityapiserver.global.exception.ErrorCode.ErrorMessage;
import com.gnimty.communityapiserver.global.validation.validator.SummonerCursorValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SummonerCursorValidator.class)
public @interface ValidateCursor {

    String message() default ErrorMessage.INVALID_INPUT_VALUE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String sortBy();

    String lastName();

    String lastSummonerMmr();

    String lastSummonerUpCount();
}
