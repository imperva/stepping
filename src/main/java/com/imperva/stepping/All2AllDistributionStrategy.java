package com.imperva.stepping;

import java.util.*;

public class All2AllDistributionStrategy implements IDistributionStrategy {

    @Override
    public void distribute(List<IStepDecorator> iStepDecorators, Data data, String subjectType) {
        Distribution[] arr = new Distribution[iStepDecorators.size()];

        for (int inc = 0; inc < iStepDecorators.size(); inc++) {
            arr[inc] = new Distribution(iStepDecorators.get(inc), data, subjectType);
        }

        distribute(arr);
    }

//    private void distribute(Distribution[] distributionList) {
//        distribute(distributionList, 0);
//    }

//    private void distribute(Distribution[] distributionList, int iterationNum) {
//        Distribution[] busy = new Distribution[distributionList.length];
//
//        for (int inc = 0; inc <= distributionList.length; inc++) {
//            if (!distributionList[inc].getiStepDecorator().offerQueueSubjectUpdate(distributionList[inc].getData(), distributionList[inc].getSubject())) {
//                busy[inc] = distributionList[inc];
//            }
//        }
//
//        if (busy.length > 0) {
//            if (iterationNum > 10) {
//                for (Distribution dist : distributionList) {
//                    dist.getiStepDecorator().queueSubjectUpdate(dist.getData(), dist.getSubject());
//                }
//            }
//            try {
//                Thread.sleep(500 * iterationNum);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            distribute(busy, ++iterationNum);
//        }
//    }

//    private void dodo(List<IStepDecorator> distributionList, Data data, String subjectType, int iterationNum) {
//        List<IStepDecorator> busy = new ArrayList<>();
//
//        for (IStepDecorator stepDecorator : distributionList) {
//            if (!stepDecorator.offerQueueSubjectUpdate(data, subjectType)) {
//                busy.add(stepDecorator);
//            }
//        }
//
//        if (busy.size() > 0) {
//            if (iterationNum > 10) {
//                for (IStepDecorator step : distributionList) {
//                    step.queueSubjectUpdate(data, subjectType);
//                }
//            }
//            try {
//                Thread.sleep(500 * iterationNum);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            dodo(busy, data, subjectType, iterationNum++);
//        }
//    }


//    void dodo3(List<Distribution> distributionList, int iterationNum) {
//        List<Distribution> busy = new ArrayList<>();
//
//        for (Distribution dist : distributionList) {
//            if (!dist.getiStepDecorator().offerQueueSubjectUpdate(dist.getData(), dist.getSubject())) {
//                busy.add(dist);
//            }
//        }
//
//        if (busy.size() > 0) {
//            if (iterationNum > 10) {
//                for (Distribution dist : distributionList) {
//                    dist.getiStepDecorator().queueSubjectUpdate(dist.getData(), dist.getSubject());
//                }
//            }
//            try {
//                Thread.sleep(500 * iterationNum);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            dodo3(busy, iterationNum++);
//        }
//    }

//    void dodo2(Distribution distribution, int iterationNum) {
//        List<Distribution> busy = new ArrayList<>();
//
//       // for (IStepDecorator stepDecorator : distributionList) {
//            if (!distribution.getiStepDecorator().offerQueueSubjectUpdate(distribution.getData(), distribution.getSubject())) {
//                busy.add(distribution);
//            }
//       // }
//
//        if (busy.size() > 0) {
//            if (iterationNum > 10) {
//
//                    distribution.getiStepDecorator().queueSubjectUpdate(distribution.getData(), distribution.getSubject());
//
//            }
//            try {
//                Thread.sleep(500 * iterationNum);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            dodo(busy, data, subjectType, iterationNum++);
//        }
//    }

//    class Distribution {
//        private Data data;
//        private IStepDecorator iStepDecorator;
//        private String subject;
//
//        Distribution() {
//        }
//
//        Distribution(IStepDecorator iStepDecorator, Data data, String subject) {
//
//            this.iStepDecorator = iStepDecorator;
//            this.data = data;
//            this.subject = subject;
//        }
//
//        public Data getData() {
//            return data;
//        }
//
//        public void setData(Data data) {
//            this.data = data;
//        }
//
//        public IStepDecorator getiStepDecorator() {
//            return iStepDecorator;
//        }
//
//        public void setiStepDecorator(IStepDecorator iStepDecorator) {
//            this.iStepDecorator = iStepDecorator;
//        }
//
//        public String getSubject() {
//            return subject;
//        }
//
//        public void setSubject(String subject) {
//            this.subject = subject;
//        }
//    }
}

