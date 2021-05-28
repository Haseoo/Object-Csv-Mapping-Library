package test.data;

import com.github.haseoo.ocm.api.annotation.*;
import lombok.Data;

import java.time.LocalDate;

@CsvEntity(name = "Issues")
@Data
public class Issue {
    @CsvId
    @CsvColumn(name = "issueId")
    private long id;
    @CsvOneToMany(endFieldName = "issues")
    private Worker worker;
    @CsvFormatter("yyyy-MM-dd")
    private LocalDate openedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue issue = (Issue) o;

        if (id != issue.id) return false;
        return openedAt.equals(issue.openedAt);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + openedAt.hashCode();
        return result;
    }
}
