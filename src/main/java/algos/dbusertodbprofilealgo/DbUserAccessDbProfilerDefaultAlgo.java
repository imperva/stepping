package algos.dbusertodbprofilealgo;

import Stepping.Algo;
import Stepping.GlobalAlgoStepConfig;

import java.util.HashMap;

public class DbUserAccessDbProfilerDefaultAlgo implements Algo {

    @Override
    public void tickCallBack() {

    }

    @Override
    public GlobalAlgoStepConfig getGlobalAlgoStepConfig() {
        return null;
    }


    @Override
    public void init() {

    }


    @Override
    public HashMap<String, Object> IoC() {
        HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("learningStep",new LearningDefaultStep());
        return objectHashMap;
    }


    @Override
    public void close() {

    }

 }
