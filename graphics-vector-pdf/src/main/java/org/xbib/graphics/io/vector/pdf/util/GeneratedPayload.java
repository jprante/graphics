package org.xbib.graphics.io.vector.pdf.util;

import java.io.IOException;

public abstract class GeneratedPayload extends Payload {

    public GeneratedPayload(boolean stream) {
        super(stream);
    }

    @Override
    public byte[] getBytes() throws IOException {
        for (byte b : generatePayload()) {
            super.write(b);
        }
        return super.getBytes();
    }

    @Override
    public void write(int b) {
        throw new UnsupportedOperationException("Payload will be calculated and is read only");
    }

    protected abstract byte[] generatePayload() throws IOException;
}

