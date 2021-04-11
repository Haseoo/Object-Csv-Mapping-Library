package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.internal.converter.*;
import com.github.haseoo.ocm.structure.CsvEntity;
import lombok.Getter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MappingContext {
    private final Map<Class, TypeConverter> converters = new HashMap<>();
    private final Map<Class, CsvEntity> registeredEntities = new HashMap<>();
    private final Map<Class, Map<Object, Object>> parsedObjects = new HashMap<>();

    @Getter
    private final String basePath;
    @Getter
    private final String splitter;

    public MappingContext(String basePath,
                   String splitter) {
        this.basePath = basePath;
        this.splitter = splitter;

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

    public <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        converters.put(clazz, converter);
    }

    public <T> void registerEntity(Class<T> clazz, CsvEntity<T> entity) {
        registeredEntities.put(clazz, entity);
    }

    @SuppressWarnings("unchecked")
    public <T> CsvEntity<T> getRegisteredEntityOrNull(Class<T> clazz) {
        if (!registeredEntities.containsKey(clazz)) {
            return null;
        }
        return registeredEntities.get(clazz);
    }

    public Object convertToObject(Class<?> clazz, String value, String formatter) {
        //TODO check if type is registered
        return converters.get(clazz).convertToType(value, formatter);
    }

    public String convertToString(Class<?> clazz, Object value, String formatter){
        //TODO check if type is registered & object is instance of class
        return converters.get(clazz).convertToString(value, formatter);
    }
}
