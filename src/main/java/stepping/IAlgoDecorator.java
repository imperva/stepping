package stepping;

public interface IAlgoDecorator extends Algo , Runnable {
    void setMessenger(IMessenger messenger);
}
