package test.data;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.UUID;

@Value
@AllArgsConstructor
public class Chip {
    public Chip() {
        uuid = UUID.randomUUID();
    }

    UUID uuid;
}
