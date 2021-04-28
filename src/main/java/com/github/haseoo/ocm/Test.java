package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;

import java.util.ArrayList;


public class Test {
    public static void main(String[] args) throws CsvMappingException {
        D c = new D();
        Foo foo = new Foo();
        Foo2 foo2 = new Foo2();
        foo.setXd(c);
        foo2.setY(1.3f);
        foo2.setFirst("XD");
        foo2.setNani(foo);
        c.setSora(foo);


        foo.setFirst("XP");

        foo.setNani(foo2);
        foo2.setNani(foo);
        var cm = new CsvMapper();
        var list = new ArrayList<BFoo>();
        //list.add(foo);
        list.add(foo2);
        cm.ListToCsv(list);
    }
}
