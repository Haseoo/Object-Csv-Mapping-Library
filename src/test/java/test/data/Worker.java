package test.data;

import com.github.haseoo.ocm.api.annotation.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@CsvEntity
public class Worker {
    @CsvId
    @CsvColumn(name = "workerId")
    private long id;
    private String name;
    private String surname;
    @CsvOneToOne(endFieldName = "workerUnderSupervision", appendToFile = true)
    private Worker supervisor;
    @CsvOneToOne(endFieldName = "supervisor")
    private Worker workerUnderSupervision;
    private Chip chip;
    @CsvManyToOne(endFieldName = "worker")
    private List<Issue> issues = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Worker worker = (Worker) o;

        if (id != worker.id) return false;
        if (!name.equals(worker.name)) return false;
        if (!surname.equals(worker.surname)) return false;
        if (!Objects.equals(supervisor, worker.supervisor)) return false;
        if (!Objects.equals(chip, worker.chip)) return false;
        return issues.containsAll(worker.issues);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + surname.hashCode();
        result = 31 * result + (supervisor != null ? supervisor.hashCode() : 0);
        result = 31 * result + (chip != null ? chip.hashCode() : 0);
        result = 31 * result + issues.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Worker{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", supervisor=" + supervisor +
                ", chip=" + chip +
                '}';
    }
}
