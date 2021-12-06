package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.settings.Settings;

import java.io.IOException;

public class ParagraphCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        state.paragraph = new Paragraph();
        if (settings.containsSetting("x") && settings.containsSetting("y")) {
            state.paragraph.setAbsolutePosition(new Position(settings.getAsFloat("x", 0f), settings.getAsFloat("y", 0f)));
        }
        if (settings.containsSetting("width")) {
            state.paragraph.setMaxWidth(settings.getAsFloat("width", state.document.getPageWidth()));
        }
        state.document.add(state.paragraph);
        engine.execute("text", state, settings);
    }
}
