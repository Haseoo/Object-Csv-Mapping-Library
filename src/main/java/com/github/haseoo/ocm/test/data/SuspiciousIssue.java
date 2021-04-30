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
public class SuspiciousIssue extends Issue {
    private double suspicionLevel;
    @CsvManyToMany(fieldName = "suspiciousIssuesNormalItems")
    private List<Item> normalItems = new ArrayList<>();
    @CsvManyToMany(fieldName = "suspiciousIssuesSuspiciousItems")
    private List<Item> suspiciousItems = new ArrayList<>();
}
