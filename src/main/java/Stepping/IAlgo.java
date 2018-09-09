package Stepping;

public abstract class IAlgo extends IRunning {

    protected IAlgo(String id) {
        super(id);
    }

    public abstract AlgoInfraConfig init();

    protected abstract void setMessenger(IMessenger messenger);
    abstract protected void IoC();
    abstract protected void newDataArrivedCallBack(Data data);
    abstract protected void tickCallBack();
}
