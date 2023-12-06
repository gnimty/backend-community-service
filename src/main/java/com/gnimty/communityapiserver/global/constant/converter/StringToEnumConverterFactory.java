package com.gnimty.communityapiserver.global.constant.converter;

import com.gnimty.communityapiserver.global.exception.BaseException;
import com.gnimty.communityapiserver.global.exception.ErrorCode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

public class StringToEnumConverterFactory implements ConverterFactory<String, Enum<?>> {

	@Override
	public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnumsConverter<>(targetType);
	}

	private record StringToEnumsConverter<T extends Enum<?>>(Class<T> enumType) implements Converter<String, T> {
		@Override
		public T convert(String source) {
			if (source.isEmpty()) {
				return null;
			}

			T[] constants = enumType.getEnumConstants();
			for (T c : constants) {
				if (c.name().equals(source.toUpperCase())) {
					return c;
				}
			}
			throw new BaseException(ErrorCode.INVALID_ENUM_VALUE);
		}
	}
}
