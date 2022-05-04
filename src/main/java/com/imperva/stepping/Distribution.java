package com.imperva.stepping;

public class Distribution {
    private Data data;
    private IStepDecorator iStepDecorator;
    private String subject;

    public Distribution() {
    }

    public Distribution(IStepDecorator iStepDecorator, Data data, String subject) {

        this.iStepDecorator = iStepDecorator;
        this.data = data;
        this.subject = subject;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public IStepDecorator getiStepDecorator() {
        return iStepDecorator;
    }

    public void setiStepDecorator(IStepDecorator iStepDecorator) {
        this.iStepDecorator = iStepDecorator;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
