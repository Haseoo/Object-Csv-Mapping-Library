package com.github.haseoo.ocm.structure.entities;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.IdFiledNotFound;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CsvClass {
    public CsvClass(Class<?> type) throws ClassIsNotAnCsvEntity {
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
    private CsvClass baseClass;
    private List<CsvClass> subClasses;

    public List<CsvField> getFieldsWithInheritance() {
        var fieldList = new ArrayList<>(fields);
        if (baseClass != null) {
            fieldList.addAll(baseClass.getFieldsWithInheritance());
        }
        return fieldList;
    }
}
