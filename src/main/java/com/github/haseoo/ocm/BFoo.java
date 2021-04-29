package com.github.haseoo.ocm;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvId;
import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import com.github.haseoo.ocm.api.annotation.CsvOneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@CsvEntity
@NoArgsConstructor
@AllArgsConstructor
public class BFoo {
    @CsvId
    private UUID id = UUID.randomUUID();
    private String first;
    @CsvOneToOne(fieldName = "randomski", appendToFile = true)
    private BFoo randomski;
    @CsvManyToMany(fieldName = "dupaDupa")
    private List<C> xd = new ArrayList<>();
}
