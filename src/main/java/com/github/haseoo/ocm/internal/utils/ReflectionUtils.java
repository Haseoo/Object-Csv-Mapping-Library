package com.github.haseoo.ocm.internal.utils;

import com.github.haseoo.ocm.api.annotation.CsvTransient;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ReflectionUtils {
    public static List<Field> getNonTransientFields(Field[] fields) {
        var fieldsToConvert = new ArrayList<Field>();
        for (var f : fields) {
            if (!containsAnnotation(f.getDeclaredAnnotations()) &&
                    !Modifier.isTransient(f.getModifiers())) {
                fieldsToConvert.add(f);
            }
        }
        return fieldsToConvert;
    }

    private static boolean containsAnnotation(Annotation[] annotations) {
        for (var annotation : annotations) {
            if (annotation.annotationType().equals(CsvTransient.class)) {
                return true;
            }
        }
        return false;
    }

    public static String getGetterName(String fieldName) {
        return String.format("get%s", fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
    }

    public static boolean isFieldAnnotatedBy(Field field, Class<? extends Annotation>... annotations) {
        for (Class<? extends Annotation> annotation : annotations) {
            if (field.isAnnotationPresent(annotation)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isClassCollection(Class<?> c) {
        return Collection.class.isAssignableFrom(c);
    }

    public static Class<?> getActualTypeArgument(Field genericField) {
        return (Class<?>) ((ParameterizedType) genericField.getGenericType())
                .getActualTypeArguments()[0];
    }
}
