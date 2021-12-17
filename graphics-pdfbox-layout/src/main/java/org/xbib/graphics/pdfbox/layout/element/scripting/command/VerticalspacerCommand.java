package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.element.VerticalSpacer;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class VerticalspacerCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        VerticalSpacer verticalSpacer = new VerticalSpacer(settings.getAsFloat("height", 0f));
        state.elements.peek().add(verticalSpacer);
    }
}
