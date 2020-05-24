package com.imperva.stepping;


public class Identifiable<T> {
    private String id;
    private T object;

    public Identifiable(T object, String id) {
        if (id == null || "".equals(id.trim()))
            throw new SteppingException("Identifiable Object must contain an ID name");

        this.id = id;
        this.object = object;
    }

    public T get() {
        return object;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Identifiable))
            throw new UnsupportedOperationException("Object must be of Identifiable type");
        Identifiable o = (Identifiable) obj;
        return this.id.toLowerCase().equals(o.getId().toLowerCase());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + (this.id.toLowerCase() != null ? this.id.toLowerCase().hashCode() : 0);
        return hash;
    }
}
