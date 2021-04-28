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
public class BFoo {
    private String first;
    @CsvOneToOne(fieldName = "nani", appendToFile = true)
    private BFoo nani;
}
