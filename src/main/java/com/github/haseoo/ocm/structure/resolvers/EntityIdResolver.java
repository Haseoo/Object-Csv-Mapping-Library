package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.api.exceptions.CsvMappingException;

public interface EntityIdResolver {
    Object getObjectById(Object id, Class<?> type) throws CsvMappingException;

    String getObjectId(Object object, Class<?> type) throws CsvMappingException;
}
