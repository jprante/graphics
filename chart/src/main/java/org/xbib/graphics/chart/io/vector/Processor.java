package org.xbib.graphics.chart.io.vector;

import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.util.PageSize;

public interface Processor {
    Document process(Iterable<Command<?>> commands, PageSize pageSize);
}

