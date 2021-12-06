package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.font.Fonts;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

public class TextCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) {
        String value = settings.get("value");
        float size = settings.getAsFloat("size", 11.0f);
        Font font = Fonts.valueOf(settings.get("font", "HELVETICA")).getFont(state.documents.peek());
        state.paragraphs.peek().addMarkup(value, size, font);
    }
}
