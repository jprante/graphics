package org.xbib.graphics.ghostscript.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingOutputStream extends ByteArrayOutputStream {

    private final Logger logger;

    public LoggingOutputStream(Logger logger) {
        super();
        this.logger = logger;
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        String s = new String(buf, 0, count);
        if (s.length() > 0) {
            logger.log(Level.FINE, s);
        }
        reset();
    }
}
