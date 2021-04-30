package com.github.haseoo.ocm.structure.files;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Value
public class CsvEntityFile implements CsvFileInfo {
    CsvEntityClass baseClass;
    String basePath;
    Map<CsvEntityClass, List<Object>> objects;

    @Override
    public String getFilePath() {
        return String.format("%s/%s", basePath, getName());
    }

    @Override
    public String getName() {
        return baseClass.getName() + ".csv";
    }

    public List<Map<String, String>> getValues() throws CsvMappingException {
        var resolvedList = new ArrayList<Map<String, String>>();
        for (Map.Entry<CsvEntityClass, List<Object>> entry : objects.entrySet()) {
            for (Object entityObject : entry.getValue()) {
                resolvedList.add(entry.getKey().resolveObject(entityObject));
            }
        }
        return resolvedList;
    }

    public Map<String, Integer> getHeader(Function<Class<?>, Optional<String>> fileNameResolver) {
        return baseClass.getCsvHeaderWithRelations(fileNameResolver);
    }

    public Map<String, Integer> getHeader() {
        return baseClass.getCsvHeaderWithoutRelations();
    }
}
