package com.imperva.stepping;

import java.io.Closeable;
import java.io.IOException;

public class RemoteController {
    private Container container;
    private Shouter shouter;
    private Closeable closeable;


    public Container getContainer() {
        return container;
    }

     void setContainer(Container container) {
        this.container = container;
    }

    public Shouter getShouter() {
        return container.getById(BuiltinTypes.STEPPING_SHOUTER.name());
    }

    public void close() throws IOException {
        closeable.close();
    }

     void setCloseable(Closeable closeable) {
        this.closeable = closeable;
    }
}
