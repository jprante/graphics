package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Locale;

public class ParagraphCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Paragraph paragraph = new Paragraph();
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            paragraph.setAbsolutePosition(new Position(settings.getAsFloat("x", 0f), settings.getAsFloat("y", 0f)));
        }
        if (settings.containsSetting("width")) {
            paragraph.setMaxWidth(settings.getAsFloat("width", 0f));
        }
        if (settings.containsSetting("alignment")) {
            paragraph.setAlignment(Alignment.valueOf(settings.get("alignment", "left").toUpperCase(Locale.ROOT)));
        }
        state.elements.push(paragraph);
        engine.executeElements(settings);
        state.elements.pop();
        state.elements.peek().add(paragraph);
    }
}
