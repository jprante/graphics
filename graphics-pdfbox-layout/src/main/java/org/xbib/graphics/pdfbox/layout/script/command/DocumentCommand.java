package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormat;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;

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
                .orientation(settings.get("orientiation", "portrait").toUpperCase(Locale.ROOT))
                .build();
        Document document = new Document(pageFormat);
        Instant instant = Instant.now();
        document.setCreationDate(instant);
        document.setModificationDate(instant);
        if (settings.containsSetting("author")) {
            document.setAuthor(settings.get("author"));
        }
        if (settings.containsSetting("creator")) {
            document.setCreator(settings.get("creator"));
        }
        if (settings.containsSetting("subject")) {
            document.setSubject(settings.get("subject"));
        }
        if (settings.containsSetting("title")) {
            document.setTitle(settings.get("title"));
        }
        state.elements.push(document);
        engine.executeElements(settings);
    }
}
