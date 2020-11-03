package org.xbib.graphics.chart.io.vector.intermediate;

import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;

public interface CommandHandler {
    void handle(Command<?> command);
}

