package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.TextElement;
import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.util.Locale;

public class TextCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) {
        String value = settings.get("value");
        float size = settings.getAsFloat("size", 11.0f);
        Document document = state.getDocument();
        Font font = Fonts.valueOf(settings.get("font", "helvetica").toUpperCase(Locale.ROOT)).getFont(document);
        state.elements.peek().add(new TextElement(value, font, size));
    }
}
