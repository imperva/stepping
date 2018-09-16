package infra;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MessageConverter {

    private JsonParser jsonParser;

    public MessageConverter() {
        jsonParser = new JsonParser();
    }

    public JsonObject convert(String value) {
        return value != null && !value.trim().isEmpty()? jsonParser.parse(value).getAsJsonObject(): null;
    }

}
