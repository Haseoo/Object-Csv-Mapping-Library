package test.data;

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
    @CsvManyToMany(endFieldName = "suspiciousIssuesNormalItems")
    private List<Item> normalItems = new ArrayList<>();
    @CsvManyToMany(endFieldName = "suspiciousIssuesSuspiciousItems")
    private List<Item> suspiciousItems = new ArrayList<>();
}
