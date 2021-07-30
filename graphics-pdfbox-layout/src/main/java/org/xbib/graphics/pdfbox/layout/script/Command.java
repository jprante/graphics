package org.xbib.graphics.pdfbox.layout.script;

public abstract class Command<T> {

    private final T value;

    public Command(T value) {
        this.value = value;
    }

    public abstract String getKey();

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s[value=%s]", getKey(), getValue());
    }
}
