package com.imperva.stepping;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


class Running extends IRunning {
    private Future future;
    private ExecutorService executorService;

    protected Running(Runnable runnable, ExecutorService executorService) {
        this.executorService = executorService;
        this.runnable = runnable;
    }

    protected Future<?> awake() {
        if (runnable != null) {
            this.future = executorService.submit(runnable);
            return future;
        }
        throw new SteppingException("Can't awake empty Runnable");
    }

    @Override
    public void close() {
        close(future, true);
    }
}