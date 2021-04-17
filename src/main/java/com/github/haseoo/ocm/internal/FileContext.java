package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.structure.entities.CsvEntityClass;
import com.github.haseoo.ocm.structure.files.CsvFileInfo;
import com.github.haseoo.ocm.structure.files.CsvFileInfoImpl;

import java.util.HashMap;
import java.util.Map;

public class FileContext {
    private final String basePath;
    private final Map<CsvEntityClass, CsvFileInfo> files = new HashMap<>();

    public FileContext(String basePath) {
        this.basePath = basePath;
    }

    public void registerFileInfo(CsvEntityClass csvEntityClass) {
        if (csvEntityClass.getBaseClass() == null) {
            return;
        }
        files.putIfAbsent(csvEntityClass, new CsvFileInfoImpl(csvEntityClass, basePath));
        registerFileInfo(csvEntityClass.getBaseClass());
    }

    public CsvFileInfo getFileForClass(Class<?> type) {
        var key = files.keySet().stream().
                filter(cc -> cc.getType().equals(type))
                .findAny()
                .orElseThrow(() -> new AssertionError("This info should be available at this point!"));
        return files.get(key);
    }
}
