package org.xbib.graphics.pdfbox.layout.script;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.script.commands.ParagraphCommand;
import java.io.IOException;
import java.io.OutputStream;

public class DocumentProcessorResult implements ProcessorResult {

    private final Document document;

    public DocumentProcessorResult(Document document) {
        this.document  = document;
    }

    @Override
    public void handle(Command<?> command) throws IOException {
        if (command instanceof ParagraphCommand) {
            ParagraphCommand paragraphCommand = (ParagraphCommand) command;
            document.add(paragraphCommand.getValue());
        }
    }

    @Override
    public void write(OutputStream out) throws IOException {
        document.render().save(out);
    }

    @Override
    public void close() throws IOException {
        document.close();
    }
}
