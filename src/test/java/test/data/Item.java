package test.data;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvId;
import com.github.haseoo.ocm.api.annotation.CsvManyToMany;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@CsvEntity(name = "Items")
@Data
public class Item {
    @CsvId
    private long id;
    private String name;
    @CsvManyToMany(endFieldName = "items")
    private List<MildlyIssue> mildlyIssues = new ArrayList<>();
    @CsvManyToMany(endFieldName = "normalItems")
    private List<SuspiciousIssue> suspiciousIssuesNormalItems = new ArrayList<>();
    @CsvManyToMany(endFieldName = "suspiciousItems")
    private List<SuspiciousIssue> suspiciousIssuesSuspiciousItems = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != item.id) return false;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        return result;
    }
}
