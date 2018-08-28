package alogs.etlalgo;

import alogs.*;
import alogs.container.Container;

import java.util.ArrayList;
import java.util.List;

public class ETLAlgo extends IAlgo {


    SubjectContainer subjectContainer = new SubjectContainer();

    public AlgoInfraConfig init() {

        Container cntr = Container.getInstance();




        //* init steps
        IStep preStep = new PreProcessStep();
        IStep aggreStep = new AggregationStep();
        List<IStep> steps = new ArrayList<IStep>();
        steps.add(preStep);
        steps.add(aggreStep);

        //* init subjects
        ISubject NewDataArrivedSubject = new NewDataArrivedSubject();
        ISubject preProcessSubject = new PreProcessSubject();

        cntr.add(preProcessSubject,"preProcessSubject");
        cntr.add(NewDataArrivedSubject,"NewDataArrivedSubject");

        subjectContainer.setSubjectsList(cntr.getTypeOf(ISubject.class));
        //* attach subjects to steps
        for (IStep step : steps) {
            step.init();
            for (ISubject subject : subjectContainer.getSubjectsList()) {
                step.attach(subject);
            }
            return null;
        }
        return null;
    }


    @Override
    public void runAlgo() {

        //* in thread
        subjectContainer.getByName("newDataArrivedSubject").occurred("get data from Q");
    }

    @Override
    protected boolean decide() {
        return false;
    }

    @Override
    public void newDataArrived(Data data) {
        //* in Q

    }

    public void shutdown() {
        //* cleanup;
    }
}