package alogs.etlalgo;

import alogs.*;
import alogs.container.Container;

import java.util.ArrayList;
import java.util.List;

public class ETLAlgo extends IAlgo {


    SubjectContainer subjectContainer = new SubjectContainer();

    public AlgoInfraConfig init() {

        Container cntr = Container.getInstance();
        cntr.add(subjectContainer, "subjectContainer");

        //* init steps
        IStep preStep = new PreProcessStep();
        IStep aggreStep = new AggregationStep();
        List<IStep> steps = new ArrayList<IStep>();
        steps.add(preStep);
        steps.add(aggreStep);

        //* init subjects
        List<ISubject> iSubjects = new ArrayList<>();
        ISubject NewDataArrivedSubject = new NewDataArrivedSubject();
        ISubject preProcessSubject = new PreProcessSubject();
        iSubjects.add(preProcessSubject);
        iSubjects.add(NewDataArrivedSubject);

        subjectContainer.setSubjectsList(cntr.getTypeOf(ISubject.class));
        //* attach subjects to steps
        for (IStep step : cntr.<IStep>getTypeOf(IStep.class)) {
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
        Container cntr = Container.getInstance();
        //* in thread
        cntr.<SubjectContainer>getById("subjectContainer").getByName("newDataArrivedSubject").occurred(new Data("sss"));

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