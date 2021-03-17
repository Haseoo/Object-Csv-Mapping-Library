package com.github.haseoo.ocm.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface CsvEntity {
    String name();
}
