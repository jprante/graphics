package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import java.io.IOException;

public interface Processor {

    ProcessorResult process(Iterable<Command<?>> commands, PageFormat pageFormat) throws IOException;
}
