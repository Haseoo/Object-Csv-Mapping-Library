package com.github.haseoo.ocm.internal.utils;

import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

@UtilityClass
public class ReflectionUtils {
    public static ArrayList<Field> getNonTransientFields(Field[] fields) {
        var fieldsToConvert = new ArrayList<Field>();
        for(var f : fields) {
            if (!containsTransient(f.getDeclaredAnnotations()) &&
                    !Modifier.isTransient(f.getModifiers())) {
                fieldsToConvert.add(f);
            }
        }
        return fieldsToConvert;
    }

    private static boolean containsTransient(Annotation[] annotations) {
        for(var annotation : annotations) {
            if (annotation.annotationType().equals(CsvTransient.class)) {
                return true;
            }
        }
        return false;
    }
}
