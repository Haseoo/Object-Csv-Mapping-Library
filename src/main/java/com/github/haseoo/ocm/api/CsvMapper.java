package com.github.haseoo.ocm.api;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.haseoo.ocm.internal.utils.ReflectionUtils.getNonTransientFields;

public class CsvMapper {
    public<T> List<T> mapFormFile(File file, Class<T> entityClass) throws IOException {
        try (
                Reader reader = new FileReader(file);
                CSVReader csvReader = new CSVReader(reader);
        ) {
            csvReader.readAll();
        }
        return new ArrayList<>();
    }

    public void arrayToCsv(Object[] objects) {
        var clazz = objects.getClass().getComponentType();
        var fields = clazz.getDeclaredFields();
        if (Arrays.stream(clazz.getDeclaredAnnotations())
                .map(Annotation::annotationType)
                .noneMatch(t -> t.equals(CsvEntity.class))) {
            //TODO throw exception
        }

        ArrayList<Field> fieldsToConvert = getNonTransientFields(fields);

        for(var f : fieldsToConvert) {
            System.out.println(f);
        }
    }



}
