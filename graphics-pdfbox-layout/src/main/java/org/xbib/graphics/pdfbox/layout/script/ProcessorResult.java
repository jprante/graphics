package org.xbib.graphics.pdfbox.layout.script;

import java.io.IOException;
import java.io.OutputStream;

public interface ProcessorResult {

    void handle(Command<?> command) throws IOException;

    void write(OutputStream out) throws IOException;

    void close() throws IOException;
}
