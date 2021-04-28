package com.github.haseoo.ocm.structure.entities;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.utils.ObjectToStringResolverContext;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import com.github.haseoo.ocm.structure.entities.fields.CsvValueField;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
public class CsvEntityClass {
    public CsvEntityClass(Class<?> type) throws ClassIsNotAnCsvEntity {
        this.type = type;
        subClasses = new ArrayList<>();
        fields = new ArrayList<>();
        var entityAnnotation = type.getDeclaredAnnotation(CsvEntity.class);
        if (entityAnnotation == null) {
            throw new ClassIsNotAnCsvEntity(type);
        }
        this.name = entityAnnotation.name().isEmpty() ? type.getSimpleName() : entityAnnotation.name();
    }

    private String name;
    private Class<?> type;
    private List<CsvField> fields;
    @Getter(value = AccessLevel.NONE)
    private CsvValueField id;
    private CsvEntityClass baseClass;
    private List<CsvEntityClass> subClasses;

    public List<CsvField> getFieldsWithInheritance() {
        var fieldList = new ArrayList<>(fields);
        if (baseClass != null) {
            fieldList.addAll(baseClass.getFieldsWithInheritance());
        }
        return fieldList;
    }

    public Optional<CsvValueField> getId() {
        if (id == null) {
            if (baseClass == null) {
                return Optional.empty();
            }
            return baseClass.getId();
        }
        return Optional.of(id);
    }

    public void addRelatedFieldsToContext(Object entityObject,
                                          ObjectToStringResolverContext context) throws CsvMappingException {
        for (CsvField csvField : getFieldsWithInheritance()) {
            try {
                csvField.validateAndAddToContext(entityObject, context);
            } catch (NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException |
                    ConstraintViolationException e) {
                throw new CsvMappingException("Csv entity object not valid", e);
            }
        }
    }
}
