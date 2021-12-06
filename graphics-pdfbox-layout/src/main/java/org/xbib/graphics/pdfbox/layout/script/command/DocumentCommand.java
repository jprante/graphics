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
        String margin = settings.get("margin", "0 0 0 0");
        String[] margins = margin.split(" ");
        PageFormat pageFormat = PageFormat.builder()
                .marginLeft(Float.parseFloat(margins[0]))
                .marginRight(Float.parseFloat(margins[1]))
                .marginTop(Float.parseFloat(margins[2]))
                .marginBottom(Float.parseFloat(margins[3]))
                .pageFormat(settings.get("format", "A4"))
                .orientation(settings.get("orientiation", "PORTRAIT"))
                .build();
        state.document = new Document(pageFormat);
        engine.execute("image", state, settings);
        engine.execute("barcode", state, settings);
        engine.execute("path", state, settings);
        engine.execute("paragraph", state, settings);
    }
}
