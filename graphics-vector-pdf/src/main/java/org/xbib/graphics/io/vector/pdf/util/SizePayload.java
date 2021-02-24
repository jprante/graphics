package org.xbib.graphics.io.vector.pdf.util;

import java.io.IOException;

public class SizePayload extends GeneratedPayload {

    private final PDFObject object;

    private final String charset;

    public SizePayload(PDFObject object, String charset, boolean stream) {
        super(stream);
        this.object = object;
        this.charset = charset;
    }

    @Override
    protected byte[] generatePayload() throws IOException {
        object.payload.close();
        String content = Integer.toString(object.payload.getBytes().length);
        return content.getBytes(charset);
    }
}
