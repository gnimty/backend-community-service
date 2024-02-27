package com.gnimty.communityapiserver.global.validation.validator;

import com.gnimty.communityapiserver.global.constant.SortBy;
import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import com.gnimty.communityapiserver.global.validation.annotation.ValidateCursor;
import java.lang.reflect.Field;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SummonerCursorValidator implements ConstraintValidator<ValidateCursor, Object> {

	private String sortBy;
	private String lastName;
	private String lastSummonerMmr;
	private String lastSummonerUpCount;
	private String lastSummonerId;

	@Override
	public void initialize(ValidateCursor constraintAnnotation) {
		sortBy = constraintAnnotation.sortBy();
		lastName = constraintAnnotation.lastName();
		lastSummonerMmr = constraintAnnotation.lastSummonerMmr();
		lastSummonerUpCount = constraintAnnotation.lastSummonerUpCount();
		lastSummonerId = constraintAnnotation.lastSummonerId();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		SortBy sortByField = getObjectValue(value, sortBy, SortBy.class);
		String lastNameField = getObjectValue(value, lastName, String.class);
		Long lastSummonerMmrField = getObjectValue(value, lastSummonerMmr, Long.class);
		Long lastSummonerUpCountField = getObjectValue(value, lastSummonerUpCount, Long.class);
		Long lastSummonerIdField = getObjectValue(value, lastSummonerId, Long.class);

		if (sortByField == null) {
			throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
		}
		if (sortByField.equals(SortBy.ATOZ)) {
			return isAllNullOrNotAllNull(lastNameField, lastSummonerIdField);
		}
		if (sortByField.equals(SortBy.TIER)) {
			return isAllNullOrNotAllNull(lastSummonerMmrField, lastSummonerIdField);
		}
		return isAllNullOrNotAllNull(lastSummonerUpCountField, lastSummonerIdField);
	}

	private <T> T getObjectValue(Object object, String fieldName, Class<T> type) {
		Class<?> clazz = object.getClass();
		Field dateField;
		try {
			dateField = clazz.getDeclaredField(fieldName);
			dateField.setAccessible(true);
			Object target = dateField.get(object);
			if (target == null) {
				return null;
			}
			if (!type.isInstance(target)) {
				throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
			}
			return type.cast(target);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new BaseException(ErrorCode.CONSTRAINT_VIOLATION);
		}
	}

	private boolean isAllNullOrNotAllNull(Object first, Object second) {
		return (first == null) == (second == null);
	}
}
