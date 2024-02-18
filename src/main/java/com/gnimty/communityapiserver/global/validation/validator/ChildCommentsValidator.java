package com.gnimty.communityapiserver.global.validation.validator;

import static com.gnimty.communityapiserver.global.constant.Bound.PARENT_COMMENTS_DEPTH;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.validation.annotation.IsChildComments;
import java.lang.reflect.Field;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChildCommentsValidator implements ConstraintValidator<IsChildComments, Object> {

    private String parentChampionCommentsId;
    private String depth;

    @Override
    public void initialize(IsChildComments constraintAnnotation) {
        this.parentChampionCommentsId = constraintAnnotation.parentChampionCommentsId();
        this.depth = constraintAnnotation.depth();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Long parentChampionCommentsId = getParentId(value);
        Integer depth = getDepth(value);

        return (parentChampionCommentsId == null && depth == PARENT_COMMENTS_DEPTH.getValue())
            || (parentChampionCommentsId != null && depth != PARENT_COMMENTS_DEPTH.getValue());
    }

    private Long getParentId(Object object) {
        Class<?> clazz = object.getClass();
        Field dateField;
        try {
            dateField = clazz.getDeclaredField(this.parentChampionCommentsId);
            dateField.setAccessible(true);
            Object target = dateField.get(object);
            if (target == null) {
                return null;
            }
            if (!(target instanceof Long)) {
                throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
            }
            return (Long) target;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
        }
    }

    private Integer getDepth(Object object) {
        Class<?> clazz = object.getClass();
        Field dateField;
        try {
            dateField = clazz.getDeclaredField(this.depth);
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
