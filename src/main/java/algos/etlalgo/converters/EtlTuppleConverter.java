package algos.etlalgo.converters;

import algos.etlalgo.dto.EtlTuple;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

//todo generic convert in stepping
public class EtlTuppleConverter {

    private Gson gson;

    public EtlTuppleConverter() {
        gson = new Gson();
    }

    public EtlTuple convert(JsonObject jsonObject) {
        return gson.fromJson(jsonObject, EtlTuple.class);
    }

}
