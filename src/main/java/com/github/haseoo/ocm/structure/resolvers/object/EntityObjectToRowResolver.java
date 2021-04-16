package com.github.haseoo.ocm.structure.resolvers.object;

import com.github.haseoo.ocm.internal.utils.ReflectionUtils;
import com.github.haseoo.ocm.structure.entities.CsvClass;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;
import lombok.Value;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Value
public class EntityObjectToRowResolver {
    CsvClass entityClass;
    Object entityObject;

    public Map<String, String> getColumnValues() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var columnValueMap = new HashMap<String, String>();
        for (CsvField csvField : entityClass.getFieldsWithInheritance()) {
            var fieldObject = entityClass.getType()
                    .getMethod(ReflectionUtils.getGetterName(csvField.getFieldName()))
                    .invoke(entityObject);
            columnValueMap.put(csvField.getColumnName(), csvField.toCsvStringValue(fieldObject));
        }
        return columnValueMap;
    }
}
