package Stepping;

public interface IMessenger<T> {
    void init();
    void emit(Data data);
    Data<T> fetching();
    void shutdown();
}
