package com.github.haseoo.ocm.test.data;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvId;
import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import com.github.haseoo.ocm.test.data.MildlyIssue;
import com.github.haseoo.ocm.test.data.SuspiciousIssue;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@CsvEntity(name = "Items")
@Data
public class Item {
    @CsvId
    private long id;
    private String name;
    @CsvManyToMany(fieldName = "items")
    private List<MildlyIssue> mildlyIssues = new ArrayList<>();
    @CsvManyToMany(fieldName = "normalItems")
    private List<SuspiciousIssue> suspiciousIssuesNormalItems = new ArrayList<>();
    @CsvManyToMany(fieldName = "suspiciousItems")
    private List<SuspiciousIssue> suspiciousIssuesSuspiciousItems = new ArrayList<>();
}
