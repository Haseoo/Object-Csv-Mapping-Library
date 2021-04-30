package com.github.haseoo.ocm.test.data;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@CsvEntity
@Data
public class MildlyIssue extends Issue {
    private int asinineCount;
    @CsvManyToMany(fieldName = "mildlyIssues")
    private List<Item> items = new ArrayList<>();
}
