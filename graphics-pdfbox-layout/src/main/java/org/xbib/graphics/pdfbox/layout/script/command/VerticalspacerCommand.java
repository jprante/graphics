package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.VerticalSpacer;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class VerticalspacerCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        VerticalSpacer verticalSpacer = new VerticalSpacer(settings.getAsFloat("height", 0f));
        state.documents.peek().add(verticalSpacer);
    }
}
