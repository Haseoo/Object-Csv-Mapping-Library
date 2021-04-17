package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@CsvEntity(name = "TestName")
public class Foo extends BFoo {
    private Integer x;
}
