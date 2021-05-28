package com.github.haseoo.ocm.structure.entities;

import com.github.haseoo.ocm.api.annotation.*;
import com.github.haseoo.ocm.api.exceptions.*;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.fields.*;
import com.github.haseoo.ocm.structure.resolvers.EntityClassResolver;
import com.github.haseoo.ocm.structure.resolvers.EntityIdResolver;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class CsvEntityClass {
    private CsvEntityClass(Class<?> type) throws ClassIsNotAnCsvEntityException {
        this.type = type;
        subClasses = new ArrayList<>();
        fields = new ArrayList<>();
        var entityAnnotation = type.getDeclaredAnnotation(CsvEntity.class);
        if (entityAnnotation == null) {
            throw new ClassIsNotAnCsvEntityException(type);
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
        return getCstHeader(field -> getColumnName(field, fileNameResolver));
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

    public void addRelatedFieldsToContext(Consumer<Class<?>> appendClass) {
        for (CsvField csvField : getFieldsWithInheritance()) {
            if (csvField.getFieldType().isAnnotationPresent(CsvEntity.class)) {
                appendClass.accept(csvField.getFieldType());
            }
        }
    }

    public Map<String, String> resolveObject(Object entityObject) throws CsvMappingException {
        var valuesMap = new HashMap<String, String>();
        valuesMap.put(CsvEntityClass.TYPE_HEADER_NAME, String.format("\"%s\"", entityObject.getClass().getCanonicalName()));
        for (CsvField csvField : getFieldsWithInheritance()) {
            if (csvField.appendToFile()) {
                valuesMap.put(csvField.getColumnName(),
                        String.format("\"%s\"", csvField.toCsvStringValue(entityObject)));
            }
        }
        return valuesMap;
    }

    public void fillObjectFields(Object registeredObject, Map<String, String> csvRow) throws CsvMappingException {
        for (CsvField csvField : getFieldsWithInheritance()) {
            if (csvField.appendToFile()) {
                csvField.setObjectField(registeredObject, csvRow);
            }
        }
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
            header.put(CsvEntityClass.TYPE_HEADER_NAME, index);
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

    public static CsvEntityClass getInstance(Class<?> type,
                                             EntityClassResolver entityClassResolver,
                                             EntityIdResolver entityIdResolver,
                                             MappingContext mappingContext) throws CsvMappingException {
        if (type == null || type.equals(Object.class)) {
            return null;
        }
        if (entityClassResolver.isClassEntityRegistered(type)) {
            return entityClassResolver.getRegisteredEntityClass(type);
        }
        var csvEntityClass = new CsvEntityClass(type);

        try {
            fillEntityClass(csvEntityClass,
                    entityIdResolver,
                    mappingContext);
        } catch (CsvMappingException e) {
            throw new CsvMappingException("Invalid entity structure", e);
        }

        var baseType = type.getSuperclass();
        var baseClassEntity = getInstance(baseType,
                entityClassResolver,
                entityIdResolver,
                mappingContext);
        if (baseClassEntity != null) {
            csvEntityClass.setBaseClass(baseClassEntity);
            baseClassEntity.getSubClasses().add(csvEntityClass);
        }
        entityClassResolver.registerEntityClass(csvEntityClass);
        return csvEntityClass;

    }

    private static void fillEntityClass(CsvEntityClass entityClass,
                                        EntityIdResolver resolverContext,
                                        MappingContext mappingContext) throws CsvMappingException {
        var type = entityClass.getType();
        List<Field> objectFields = ReflectionUtils.getNonTransientFields(type.getDeclaredFields());
        for (Field objectField : objectFields) {
            entityClass.getFields().add(resolveField(type,
                    objectField,
                    resolverContext,
                    mappingContext));
            if (objectField.isAnnotationPresent(CsvId.class)) {
                entityClass.setId(new CsvValueField(mappingContext.getConverterContext(), objectField));
            }
        }

    }

    private static CsvField resolveField(Class<?> entityType,
                                         Field entityField,
                                         EntityIdResolver resolverContext,
                                         MappingContext mappingContext) throws
            ClassIsNotAnCsvEntityException,
            FieldIsNotACollectionException,
            RelationEndNotPresentException {
        CsvField csvField;
        if (entityField.isAnnotationPresent(CsvOneToOne.class)) {
            csvField = CsvOneToOneField.newInstance(entityType, entityField, resolverContext);
        } else if (entityField.isAnnotationPresent(CsvOneToMany.class)) {
            csvField = CsvOneToManyField.newInstance(entityType, entityField, resolverContext);
        } else if (entityField.isAnnotationPresent(CsvManyToOne.class)) {
            csvField = CsvManyToOneField.newInstance(entityType, entityField);
        } else if (entityField.isAnnotationPresent(CsvManyToMany.class)) {
            csvField = CsvManyToManyField.newInstance(entityType,
                    entityField,
                    resolverContext,
                    mappingContext.getSplitter());
        } else {
            csvField = new CsvValueField(mappingContext.getConverterContext(), entityField);
        }
        return csvField;
    }

    public static final String TYPE_HEADER_NAME = "#ENTITY_TYPE";
}
