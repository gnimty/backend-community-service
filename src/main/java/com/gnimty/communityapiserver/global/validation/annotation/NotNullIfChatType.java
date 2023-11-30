package com.gnimty.communityapiserver.global.validation.annotation;

import com.gnimty.communityapiserver.global.validation.validator.ChatTypeValidator;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ChatTypeValidator.class)
public @interface NotNullIfChatType {

    String message() default "Chat 타입 시 data는 필수입니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default {};

}
