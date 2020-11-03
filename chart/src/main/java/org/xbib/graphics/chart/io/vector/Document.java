package org.xbib.graphics.chart.io.vector;

import org.xbib.graphics.chart.io.vector.intermediate.CommandHandler;

import java.io.IOException;
import java.io.OutputStream;

public interface Document extends CommandHandler {
    void write(OutputStream out) throws IOException;

    void close();
}

