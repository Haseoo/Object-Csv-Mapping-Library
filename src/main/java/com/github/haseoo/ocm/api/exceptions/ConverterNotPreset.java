package com.github.haseoo.ocm.api.exceptions;

public class ConverterNotPreset extends CsvMappingException {
    public ConverterNotPreset(Class<?> type) {
        super(String.format("Converter of type %s not registered.", type));
    }
}
