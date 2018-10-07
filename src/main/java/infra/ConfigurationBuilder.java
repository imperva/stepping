package infra;

import java.util.List;
import java.util.Map;

public interface ConfigurationBuilder <T> {

    Map<String, List<T>> getConfig(IdGenerator idGenerator);

}
