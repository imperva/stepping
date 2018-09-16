package alogs.etlalgo.converters;

import alogs.etlalgo.dto.EtlTupple;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

//todo generic convert in stepping
public class EtlTuppleConverter {

    private Gson gson;

    public EtlTuppleConverter() {
        gson = new Gson();
    }

    public EtlTupple convert(JsonObject jsonObject) {
        return gson.fromJson(jsonObject, EtlTupple.class);
    }

}
