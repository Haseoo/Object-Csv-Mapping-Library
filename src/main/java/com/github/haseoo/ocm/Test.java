package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvColumn;
import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvTransient;
import com.github.haseoo.ocm.api.converter.TypeConverter;
import com.github.haseoo.ocm.internal.MappingContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;


public class Test {
    public static void main(String[] args) throws IOException {
        final var list = new ArrayList<Foo>();
        list.add(new Foo(8L,11, "12", 13, "t14", BigDecimal.TEN, 3.14));
        list.add(new Foo(8L, 21, "22", 23, "t\"24", BigDecimal.ZERO, 7D));
        final var arr = list.toArray(new Foo[0]);
        var mc = new MappingContext("", ";");
        mc.registerConverter(int[].class, new TypeConverter<>() {
            @Override
            public int[] convertToType(String value, String formatter) {
                return new int[0];
            }

            @Override
            public String convertToString(int[] value, String formatter) {
                return "\"" + Arrays.toString(value) + "\"";
            }
        });
        var ce = com.github.haseoo.ocm.structure.CsvEntity.newInstance(mc, Foo.class, arr, true);
        var rows = ce.resolveToString();
        rows.forEach(System.out::println);
    }
}
