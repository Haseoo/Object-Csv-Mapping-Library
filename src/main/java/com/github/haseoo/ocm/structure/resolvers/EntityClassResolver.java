package com.github.haseoo.ocm.structure.resolvers;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;

public interface EntityClassResolver {
    boolean isClassEntityRegistered(Class<?> type);

    CsvEntityClass getRegisteredEntityClass(Class<?> type);

    void registerEntityClass(CsvEntityClass csvEntityClass);
}
