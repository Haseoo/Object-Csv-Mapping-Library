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
public class C {
    @CsvId
    private String id = UUID.randomUUID().toString();
    @CsvManyToMany(fieldName = "xd")
    private List<BFoo> dupaDupa = new ArrayList<>();
}
