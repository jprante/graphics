package org.xbib.graphics.io.vector;

import java.io.IOException;

public interface Processor {

    ProcessorResult process(Iterable<Command<?>> commands, PageSize pageSize) throws IOException;
}
