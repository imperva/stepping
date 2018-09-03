package Stepping;

public abstract class IAlgo extends IRunning {


    protected IAlgo(String id) { super(id);
    }

    public abstract AlgoInfraConfig init();
    public abstract void newDataArrived(Data<?> data);
    public abstract void setMessenger(IMessenger messenger);
}
