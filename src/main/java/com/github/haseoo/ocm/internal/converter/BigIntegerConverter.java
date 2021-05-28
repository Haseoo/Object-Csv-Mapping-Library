package com.github.haseoo.ocm.internal.converter;

import com.github.haseoo.ocm.api.converter.TypeConverter;

import java.math.BigInteger;

public class BigIntegerConverter implements TypeConverter<BigInteger> {
    @Override
    public BigInteger convertToTypeObject(String value, String formatter) {
        return new BigInteger(value);
    }

    @Override
    public String convertToString(BigInteger value, String formatter) {
        return value.toString();
    }
}
