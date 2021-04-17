package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import com.github.haseoo.ocm.internal.MappingContext;
import com.github.haseoo.ocm.structure.entities.fields.CsvField;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;


public class Test {
    public static void main(String[] args) throws CsvMappingException {
        Foo foo = new Foo();
        foo.setX(5);
        foo.setFirst("FIRST");
        Foo2 foo2 = new Foo2();
        foo2.setFirst("XDDDDS");
        foo2.setY(3.14f);
        var cm = new CsvMapper();
        var list = new ArrayList<BFoo>();
        list.add(foo);
        list.add(foo2);
        list.add(new BFoo("SECOND"));
        cm.arrayToCsv(list);
    }
}
