package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.internal.converter.*;
import com.github.haseoo.ocm.structure.CsvEntity;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ConverterContext {
    private final Map<Class, TypeConverter> converters = new HashMap<>();

    public ConverterContext() {
        registerConverter(LocalDate.class, new DateConverter());

        registerConverter(LocalDateTime.class, new DateTimeConverter());

        registerConverter(Long.class, new LongConverter());
        registerConverter(long.class, new LongConverter());

        registerConverter(Integer.class, new IntConverter());
        registerConverter(int.class, new IntConverter());

        registerConverter(Double.class, new DoubleConverter());
        registerConverter(double.class, new DoubleConverter());

        registerConverter(Float.class, new FloatConverter());
        registerConverter(float.class, new FloatConverter());

        registerConverter(BigDecimal.class, new BigDecimalConverter());
        registerConverter(BigInteger.class, new BigIntegerConverter());
        registerConverter(String.class, new StringConverter());
    }

    public <T> void registerConverter(Class<T> type, TypeConverter<T> converter) {
        converters.put(type, converter);
    }

    public Object convertToObject(Class<?> type, String value, String formatter) {
        //TODO check if type is registered
        return converters.get(type).convertToType(value, formatter);
    }

    @SuppressWarnings("unchecked")
    public String convertToString(Class<?> type, Object value, String formatter){
        //TODO check if type is registered & object is instance of class
        return converters.get(type).convertToString(value, formatter);
    }
}
