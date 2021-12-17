package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.element.ControlElement;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class NewpageCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ControlElement controlElement = ControlElement.NEWPAGE;
        state.elements.peek().add(controlElement);
    }
}