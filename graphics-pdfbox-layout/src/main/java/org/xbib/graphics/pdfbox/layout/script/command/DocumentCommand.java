package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class DocumentCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PageFormat pageFormat = PageFormat.builder()
                .pageFormat(settings.get("format", "A4"))
                .orientation(settings.get("orientiation", "PORTRAIT"))
                .build();
        state.document = new Document(pageFormat);
        engine.execute("paragraoh", settings);
    }
}
