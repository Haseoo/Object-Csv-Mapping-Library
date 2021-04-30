package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.api.converter.TypeConverter;
import lombok.Getter;

public class MappingContext {
    @Getter
    private final ConverterContext converterContext;

    @Getter
    private final String basePath;
    @Getter
    private final String splitter;

    public MappingContext(String basePath,
                          String splitter) {
        this.basePath = basePath;
        this.splitter = splitter;
        this.converterContext = new ConverterContext();
    }

    public <T> void registerConverter(Class<T> clazz, TypeConverter<T> converter) {
        converterContext.registerConverter(clazz, converter);
    }
}
