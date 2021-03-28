package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.internal.converter.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MappingContext {
    private Map<Class<?>, TypeConverter<?>> converters = new HashMap<>();

    MappingContext() {
        converters.put(LocalDate.class, new DateConverter());

        converters.put(LocalDateTime.class, new DateTimeConverter());

        converters.put(Long.TYPE, new LongConverter());
        converters.put(long.class, new LongConverter());

        converters.put(Integer.TYPE, new IntConverter());
        converters.put(int.class, new IntConverter());

        converters.put(Double.TYPE, new DoubleConverter());
        converters.put(double.class, new DoubleConverter());

        converters.put(Float.TYPE, new FloatConverter());
        converters.put(float.class, new FloatConverter());

        converters.put(BigDecimal.class, new BigDecimalConverter());
        converters.put(BigInteger.class, new BigIntegerConverter());
        converters.put(String.class, new StringConverter());
    }

    public <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        converters.put(clazz, converter);
    }

    public Object convertToObject(Class<?> clazz, String value, String formatter) {
        //TODO check if type is registered
        return converters.get(clazz).convertToType(value, formatter);
    }
}
