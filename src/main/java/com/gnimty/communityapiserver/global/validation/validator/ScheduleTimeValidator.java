package com.gnimty.communityapiserver.global.validation.validator;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.validation.annotation.BeforeEndTime;
import java.lang.reflect.Field;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ScheduleTimeValidator implements ConstraintValidator<BeforeEndTime, Object> {

    private String startTime;
    private String endTime;

    @Override
    public void initialize(BeforeEndTime constraintAnnotation) {
        startTime = constraintAnnotation.startTime();
        endTime = constraintAnnotation.endTime();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Integer start = getFieldValue(value, startTime);
        Integer end = getFieldValue(value, endTime);
        return start < end;
    }

    private Integer getFieldValue(Object object, String fieldName) {
        Class<?> clazz = object.getClass();
        Field dateField;
        try {
            dateField = clazz.getDeclaredField(fieldName);
            dateField.setAccessible(true);
            Object target = dateField.get(object);
            if (!(target instanceof Integer)) {
                throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
            }
            return (Integer) target;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
        }
    }
}
