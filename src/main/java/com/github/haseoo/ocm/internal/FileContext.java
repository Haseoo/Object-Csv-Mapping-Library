package com.github.haseoo.ocm.internal;

import com.github.haseoo.ocm.structure.entities.CsvClass;
import com.github.haseoo.ocm.structure.files.CsvFileInfo;
import com.github.haseoo.ocm.structure.files.CsvFileInfoImpl;

import java.util.HashMap;
import java.util.Map;

public class FileContext {
    private final String basePath;
    private final Map<CsvClass, CsvFileInfo> files = new HashMap<>();

    public FileContext(String basePath) {
        this.basePath = basePath;
    }

    public void registerFileInfo(CsvClass csvClass) {
        if (csvClass.getBaseClass() == null) {
            return;
        }
        files.putIfAbsent(csvClass, new CsvFileInfoImpl(csvClass, basePath));
        registerFileInfo(csvClass.getBaseClass());
    }

    public CsvFileInfo getFileForClass(Class<?> type) {
        var key = files.keySet().stream().
                filter(cc -> cc.getType().equals(type))
                .findAny()
                .orElseThrow(() -> new AssertionError("This info should be available at this point!"));
        return files.get(key);
    }
}
