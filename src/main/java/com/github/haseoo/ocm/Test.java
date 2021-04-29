package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;

import java.util.ArrayList;


public class Test {
    public static void main(String[] args) throws CsvMappingException {
        C c = new C();
        Foo foo = new Foo();
        Foo2 foo2 = new Foo2();
        foo.getXd().add(c);
        foo2.setY(1.3f);
        foo2.setFirst("XD");
        foo2.setRandomski(foo);
        c.getDupaDupa().add(foo);
        c.getDupaDupa().add(foo2);


        foo.setFirst("XP");

        foo.setRandomski(foo2);
        foo2.getXd().add(c);
        foo2.setRandomski(foo);
        var cm = new CsvMapper("", "|");
        var list = new ArrayList<BFoo>();
        //list.add(foo);
        list.add(foo2);
        list.add(foo2);
        list.add(foo2);
        System.out.println(cm.listToCstInMemoryFile(list));
    }
}
