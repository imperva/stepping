package Stepping;

public interface IMessenger<T> {
    void init();
    void emit(Data<T> data);
    Data<T> fetching();
    void shutdown();
}
