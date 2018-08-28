package alogs;

public abstract class IAlgo {
    public abstract AlgoInfraConfig init();
    public abstract void newDataArrived(Data data);
    public abstract void runAlgo();
    protected abstract boolean decide();
}
