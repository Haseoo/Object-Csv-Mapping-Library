package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvOneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@CsvEntity
@NoArgsConstructor
@AllArgsConstructor
public class C {
    @CsvOneToOne(fieldName = "xd")
    private BFoo sora;
}
