package com.github.haseoo.ocm.test.data;

import com.github.haseoo.ocm.api.annotation.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@CsvEntity
public class Worker {
    @CsvId
    @CsvColumn(name = "workerId")
    private long id;
    private String name;
    private String surname;
    @CsvOneToOne(fieldName = "workerUnderSupervision", appendToFile = true)
    private Worker supervisor;
    @CsvOneToOne(fieldName = "supervisor")
    private Worker workerUnderSupervision;
    private Chip chip;
    @CsvManyToOne(fieldName = "worker")
    private List<Issue> issues = new ArrayList<>();
}
