package com.github.haseoo.ocm.test.data;

import com.github.haseoo.ocm.api.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@CsvEntity(name = "Issues")
@Data
public class Issue {
    @CsvId
    @CsvColumn(name = "issueId")
    private long id;
    @CsvOneToMany(fieldName = "issues")
    private Worker worker;
    @CsvFormatter("yyyy-MM-dd")
    private LocalDate openedAt;
}
