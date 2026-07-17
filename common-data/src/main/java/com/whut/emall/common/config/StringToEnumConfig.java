package com.whut.emall.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.whut.emall.common.entity.enums.IEmallEnum;

@Configuration
public class StringToEnumConfig implements WebMvcConfigurer{
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumFactory());
    }
}

class StringToEnumFactory implements ConverterFactory<String, Enum<?>> {
    @Override
    public <T extends Enum<?>> Converter<String, T> getConverter(Class<T> targetType) {
        if (!IEmallEnum.class.isAssignableFrom(targetType)) {
            return null;
        }
        return new StringToEnumConverter<>(targetType);
    }

    private static class StringToEnumConverter<T extends Enum<?>> implements Converter<String, T> {
        private final Class<T> enumType;

        public StringToEnumConverter(final Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            if (source == null) return null;
            source = source.trim();
            if (source.isEmpty()) return null;
            T[] constants = enumType.getEnumConstants();
            if (constants == null) return null;
            
            for (T constant : constants) {
                IEmallEnum emallEnum = (IEmallEnum) constant;
                if (source.equals(emallEnum.getDesc()) || source.equalsIgnoreCase(constant.name())) {
                    return constant;
                }
                Integer enumValue = emallEnum.getValue();
                if (enumValue != null && source.equals(enumValue.toString())) {
                    return constant;
                }
            }
            return null;
        }
    }
}