import com.imperva.stepping.*;

public class StepMapper1 implements StepMapper {

    @Override
    public void init(Container cntr, Shouter shouter) {

    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType) {

    }

    @Override
    public void onRestate() {

    }

    @Override
    public void onKill() {

    }

    @Override
    public void onSubjectUpdate(Data data, String subjectType, Reducer reducer) {
        reducer.reduce(data, subjectType);

    }
}
