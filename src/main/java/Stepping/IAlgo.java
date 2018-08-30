package Stepping;

public abstract class IAlgo extends IRunning {
    protected IAlgo(String id, int delay, int initialdelay) {
        super(id, delay, initialdelay);
    }

    public abstract AlgoInfraConfig init();
    public abstract void newDataArrived(Data data);
}
