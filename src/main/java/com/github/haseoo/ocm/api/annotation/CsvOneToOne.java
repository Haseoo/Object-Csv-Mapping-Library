package com.github.haseoo.ocm.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CsvOneToOne {
    String endFieldName();

    boolean appendToFile() default false;
}
