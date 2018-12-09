package stepping;


public interface IBuiltinExceptionHandler extends IExceptionHandler {
    void handle(SteppingException e);

    void handle(SteppingSystemException e);
}
