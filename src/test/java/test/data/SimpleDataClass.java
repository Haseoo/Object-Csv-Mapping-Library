package test.data;

import com.github.haseoo.ocm.api.annotation.CsvEntity;
import com.github.haseoo.ocm.api.annotation.CsvFormatter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@CsvEntity
@NoArgsConstructor
@AllArgsConstructor
public class SimpleDataClass {
    private Long longVal;
    private String stringVal;
    private Integer intVal;
    @CsvFormatter("yyyy.MM.dd HH:mm")
    private LocalDateTime localDateTime;
}
