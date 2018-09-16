package infra;

import java.util.UUID;

public class RandomIdGenerator implements IdGenerator {

    @Override
    public String get() {
        return UUID.randomUUID().toString();
    }

}
