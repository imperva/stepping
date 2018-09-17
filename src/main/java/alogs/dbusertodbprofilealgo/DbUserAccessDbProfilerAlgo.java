package alogs.dbusertodbprofilealgo;

import Stepping.AlgoBase;
import Stepping.Data;

public class DbUserAccessDbProfilerAlgo extends AlgoBase {

    public DbUserAccessDbProfilerAlgo() {
        super(DbUserAccessDbProfilerAlgo.class.getName());
    }

    @Override
    protected void tickCallBack() {

    }


    @Override
    protected void IoC() {
        super.IoC();

        //* init steps
        DI(new LearningStep(), "learningStep");
    }
}
