package com.github.haseoo.ocm.structure.resolvers.object;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import lombok.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Value
public class EntityObjectToRowResolver {
    CsvEntityClass entityClass;
    Object entityObject;

    public Map<String, String> getColumnValues() throws CsvMappingException {
        var columnValueMap = new HashMap<String, String>();
        for (CsvField csvField : entityClass.getFieldsWithInheritance()) {
            try {
                var fieldObject = entityClass.getType()
                        .getMethod(ReflectionUtils.getGetterName(csvField.getFieldName()))
                        .invoke(entityObject);
                columnValueMap.put(csvField.getColumnName(), csvField.toCsvStringValue(fieldObject));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | CsvMappingException e) {
                throw new CsvMappingException(String.format("While parsing class %s", entityClass.getType().getCanonicalName()), e);
            }
        }
        return columnValueMap;
    }
}
