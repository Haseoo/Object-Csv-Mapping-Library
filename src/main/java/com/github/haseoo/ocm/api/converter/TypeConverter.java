package com.github.haseoo.ocm.api.converter;

public interface TypeConverter<T> {
    T convertToType(String value, String formatter);

    String convertToString(T value, String formatter);
}
