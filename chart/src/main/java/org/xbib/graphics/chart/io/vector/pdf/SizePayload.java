package org.xbib.graphics.chart.io.vector.pdf;

import org.xbib.graphics.chart.io.vector.util.DataUtils;

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
    protected byte[] generatePayload() {
        try {
            object.payload.close();
            String content = DataUtils.format(object.payload.getBytes().length);
            return content.getBytes(charset);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

