package com.github.haseoo.ocm.structure.entities;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.exceptions.ClassIsNotAnCsvEntity;
import com.github.haseoo.ocm.api.exceptions.ConstraintViolationException;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import com.github.haseoo.ocm.structure.entities.fields.CsvValueField;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public Map<String, Integer> getCsvHeaderWithRelations(Function<Class<?>, Optional<String>> fileNameResolver) {
        return getCstHeader((field) -> getColumnName(field, fileNameResolver));
    }

    public Map<String, Integer> getCsvHeaderWithoutRelations() {
        return getCstHeader(CsvField::getColumnName);
    }

    public void addRelatedFieldsToContext(Object entityObject,
                                          Consumer<Object> appendObject) throws CsvMappingException {
        for (CsvField csvField : getFieldsWithInheritance()) {
            try {
                csvField.validateAndAddToContext(entityObject, appendObject);
            } catch (NoSuchMethodException |
                    InvocationTargetException |
                    IllegalAccessException |
                    ConstraintViolationException e) {
                throw new CsvMappingException("Csv entity object not valid", e);
            }
        }
    }

    public Map<String, String> resolveObject(Object entityObject) throws CsvMappingException {
        var valuesMap = new HashMap<String, String>();
        valuesMap.put("TYPE", String.format("\"%s\"", entityObject.getClass().getCanonicalName()));
        for (CsvField csvField : getFieldsWithInheritance()) {
            if (csvField.appendToFile()) {
                valuesMap.put(csvField.getColumnName(),
                        String.format("\"%s\"", csvField.toCsvStringValue(entityObject)));
            }
        }
        return valuesMap;
    }

    private String getColumnName(CsvField csvField, Function<Class<?>,
            Optional<String>> fileNameResolver) {
        var columnName = new StringBuilder(csvField.getColumnName());
        fileNameResolver.apply(csvField.getFieldType())
                .ifPresent(fName -> columnName.append("@")
                        .append(fName));
        return columnName.toString();
    }

    private List<CsvField> getSubClassesFields() {
        var fieldList = new ArrayList<>(fields);
        for (CsvEntityClass subClass : subClasses) {
            fieldList.addAll(subClass.getSubClassesFields());
        }
        return fieldList;
    }

    private List<CsvField> getCsvFieldsForHeader() {
        var columnFields = getFieldsWithInheritance();
        columnFields.removeAll(this.fields);
        columnFields.addAll(getSubClassesFields());
        return columnFields;
    }

    private Map<String, Integer> getCstHeader(Function<CsvField, String> columnName) {
        List<CsvField> columnFields = getCsvFieldsForHeader();
        var header = new HashMap<String, Integer>();
        int index = 0;
        for (CsvField field : columnFields) {
            if (field.appendToFile()) {
                header.put(columnName.apply(field), index++);
            }
        }
        if (!subClasses.isEmpty()) {
            header.put("TYPE", index);
        }
        return header;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvEntityClass that = (CsvEntityClass) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(type, that.type)) return false;
        if (!Objects.equals(fields, that.fields)) return false;
        if (!Objects.equals(id, that.id)) return false;
        return Objects.equals(baseClass, that.baseClass);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CsvEntityClass{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", fields=" + fields +
                ", id=" + id +
                '}';
    }
}
