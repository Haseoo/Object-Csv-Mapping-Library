package test;

import com.github.haseoo.ocm.api.CsvMapper;
import com.github.haseoo.ocm.api.exceptions.CsvMappingException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import test.data.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tests {

    @Test
    void complexEntitiesTest() throws CsvMappingException, IOException {
        var mapper = new CsvMapper("", ";");
        mapper.registerConverter(Chip.class, new ChipConverter());
        var in = getWorkers();
        var files = mapper.listToCsvInMemoryFile(in);
        var out = mapper.filesToList(Worker.class, files);
        Assertions.assertThat(out).containsAll(in);
    }

    @Test
    void simpleEntityTest() throws CsvMappingException, IOException {
        ArrayList<SimpleDataClass> in = getSimpleDataClasses();
        var mapper = new CsvMapper(";");
        var files = mapper.listToCsvInMemoryFile(in);
        var out = mapper.filesToList(SimpleDataClass.class, files);
        Assertions.assertThat(out).containsAll(in);
    }

    private static ArrayList<SimpleDataClass> getSimpleDataClasses() {
        var in = new ArrayList<SimpleDataClass>();
        in.add(new SimpleDataClass(null, "test", 5, LocalDateTime.of(2021, 1, 1, 3, 5)));
        in.add(new SimpleDataClass(1L, null, 5, LocalDateTime.of(2019, 1, 1, 3, 5)));
        in.add(new SimpleDataClass(2137L, "test2", null, LocalDateTime.of(2021, 1, 1, 3, 5)));
        in.add(new SimpleDataClass(5L, "test2", 14, null));
        return in;
    }

    private static List<Worker> getWorkers() {
        var boss = new Worker();
        boss.setId(0);
        boss.setName("Tomasz");
        boss.setSurname("Problem");
        boss.setChip(new Chip());

        var worker1 = new Worker();
        worker1.setId(1);
        worker1.setName("Name1");
        worker1.setSurname("Surname1");
        worker1.setChip(new Chip());
        worker1.setSupervisor(boss);
        boss.setWorkerUnderSupervision(worker1);

        var worker2 = new Worker();
        worker2.setId(2);
        worker2.setName("Name2");
        worker2.setSurname("Surname2");
        worker2.setSupervisor(worker1);
        worker2.setChip(new Chip());
        worker1.setWorkerUnderSupervision(worker2);

        var worker3 = new Worker();
        worker3.setId(3);
        worker3.setName("Name3");
        worker3.setSurname("Surname3");
        worker3.setSupervisor(worker2);
        worker2.setWorkerUnderSupervision(worker3);


        var issue1 = new Issue();
        issue1.setId(1);
        issue1.setOpenedAt(LocalDate.of(2021, 1, 12));
        var issue2 = new MildlyIssue();
        issue2.setId(2);
        issue2.setOpenedAt(LocalDate.of(2021, 2, 22));
        issue2.setAsinineCount(15);
        var issue3 = new MildlyIssue();
        issue3.setId(3);
        issue3.setOpenedAt(LocalDate.of(2021, 3, 7));
        issue2.setAsinineCount(25);
        var susIssue = new SuspiciousIssue();
        susIssue.setId(4);
        susIssue.setOpenedAt(LocalDate.of(2021, 4, 7));
        susIssue.setSuspicionLevel(420.69);

        var item1 = new Item();
        item1.setId(1);
        item1.setName("item1");
        var item2 = new Item();
        item2.setId(2);
        item2.setName("item2");
        var item3 = new Item();
        item3.setId(3);
        item3.setName("item3");
        var item4 = new Item();
        item4.setId(4);
        item4.setName("item4");
        var item5 = new Item();
        item5.setId(5);
        item5.setName("item5");
        var item6 = new Item();
        item6.setId(6);
        item6.setName("item6");

        issue1.setWorker(worker1);
        worker1.getIssues().add(issue1);
        issue2.setWorker(worker1);
        worker1.getIssues().add(issue2);
        issue3.setWorker(worker3);
        worker3.getIssues().add(issue3);


        issue2.getItems().add(item1);
        issue2.getItems().add(item2);
        issue2.getItems().add(item3);
        item1.getMildlyIssues().add(issue2);
        item2.getMildlyIssues().add(issue2);
        item3.getMildlyIssues().add(issue2);

        issue3.getItems().add(item1);
        issue3.getItems().add(item2);
        issue3.getItems().add(item4);
        issue3.getItems().add(item5);
        item1.getMildlyIssues().add(issue3);
        item2.getMildlyIssues().add(issue3);
        item4.getMildlyIssues().add(issue3);
        item5.getMildlyIssues().add(issue3);

        susIssue.getNormalItems().add(item1);
        susIssue.getNormalItems().add(item2);
        susIssue.getSuspiciousItems().add(item6);
        item1.getSuspiciousIssuesNormalItems().add(susIssue);
        item2.getSuspiciousIssuesNormalItems().add(susIssue);
        item6.getSuspiciousIssuesSuspiciousItems().add(susIssue);
        return Arrays.asList(boss, worker1, worker2, worker3);
    }
}
