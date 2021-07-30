package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import java.io.IOException;

public class DocumentProcessor implements Processor {

    @Override
    public ProcessorResult process(Iterable<Command<?>> commands, PageFormat pageFormat) throws IOException {
        ProcessorResult processorResult = new DocumentProcessorResult(new Document(pageFormat));
        for (Command<?> command : commands) {
            processorResult.handle(command);
        }
        processorResult.close();
        return processorResult;
    }
}
