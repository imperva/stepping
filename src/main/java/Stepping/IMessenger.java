package Stepping;

public interface IMessenger {
    void init();
    void emit(Data data);
    void fetching(IExternalDataReceiver  dataReceiver);
}
