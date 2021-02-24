package org.xbib.graphics.io.vector.pdf.util;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

public class Payload extends OutputStream {

    private final ByteArrayOutputStream byteStream;

    private final boolean stream;

    private OutputStream filteredStream;

    private boolean empty;

    public Payload(boolean stream) {
        this.byteStream = new ByteArrayOutputStream();
        this.stream = stream;
        this.filteredStream = byteStream;
        this.empty = true;
    }

    public byte[] getBytes() throws IOException {
        return byteStream.toByteArray();
    }

    public boolean isStream() {
        return stream;
    }

    @Override
    public void write(int b) throws IOException {
        filteredStream.write(b);
        empty = false;
    }

    @Override
    public void close() throws IOException {
        super.close();
        filteredStream.close();
    }

    public void addFilter(Class<? extends FilterOutputStream> filterClass)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (!empty) {
            throw new IllegalStateException("unable to add filter after writing to payload");
        }
        filteredStream = filterClass.getConstructor(OutputStream.class).newInstance(filteredStream);
    }
}

