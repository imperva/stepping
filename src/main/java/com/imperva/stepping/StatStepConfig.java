package com.imperva.stepping;

public class StatStepConfig {

    private final Boolean enable;

    public StatStepConfig(){
        SteppingProperties stepProp = SteppingProperties.getInstance();
        enable = new Boolean(stepProp.getProperty("stepping.default.algo.statstep.enable"));
    }

    public Boolean isEnable() {
        return enable;
    }
}
