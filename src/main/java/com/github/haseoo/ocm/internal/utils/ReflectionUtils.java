package com.github.haseoo.ocm.internal.utils;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.api.exceptions.FieldIsNotACollectionException;
import com.github.haseoo.ocm.api.exceptions.RelationEndNotPresentException;
import javassist.ClassPath;
import lombok.experimental.UtilityClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    public static Object getFieldValue(Object object, String fieldName) throws CsvMappingException {
        try {
            return object.getClass().getMethod(getGetterName(fieldName)).invoke(object);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CsvMappingException("Getter accessor not present or invalid", e);
        }
    }

    public static void validateRelationClass(Class<?> relationEndEntityType) throws ClassIsNotAnCsvEntity {
        if (!relationEndEntityType.isAnnotationPresent(CsvEntity.class)) {
            throw new ClassIsNotAnCsvEntity(relationEndEntityType);
        }
    }

    public static void setObjectFiled(Object obj,
                                      Object value,
                                      String fieldName,
                                      Class<?> fieldType) throws CsvMappingException {
        var getterName = String.format("get%s", fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
        try {
            obj.getClass().getMethod(getterName, fieldType).invoke(obj, value);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new CsvMappingException("Invalid or not present getter method", e);
        }
    }

    public static Field getRelationField(Class<?> relationEndEntityType,
                                         String fieldName,
                                         Class<? extends Annotation> annotationType) throws RelationEndNotPresentException {
        Field endRelationField;
        try {
            endRelationField = relationEndEntityType.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RelationEndNotPresentException(annotationType, relationEndEntityType, fieldName);
        }
        return endRelationField;
    }

    public static void validateCollectionRelation(Class<?> relationBeginEntityType, String fieldName) throws FieldIsNotACollectionException {
        if (!ReflectionUtils.isClassCollection(relationBeginEntityType)) {
            throw new FieldIsNotACollectionException(relationBeginEntityType,
                    fieldName);
        }
    }

    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
