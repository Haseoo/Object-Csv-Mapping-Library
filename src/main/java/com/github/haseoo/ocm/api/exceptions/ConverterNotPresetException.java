package com.github.haseoo.ocm.api.exceptions;

public class ConverterNotPresetException extends CsvMappingException {
    public ConverterNotPresetException(Class<?> type) {
        super(String.format("Converter of type %s not registered.", type));
    }
}
