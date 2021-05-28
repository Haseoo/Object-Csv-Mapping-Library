package com.github.haseoo.ocm.api.converter;

public interface TypeConverter<T> {
    T convertToTypeObject(String value, String formatter);

    String convertToString(T value, String formatter);
}
