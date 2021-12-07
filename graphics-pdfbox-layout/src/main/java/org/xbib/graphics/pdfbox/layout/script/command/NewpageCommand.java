package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.ControlElement;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class NewpageCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ControlElement controlElement = ControlElement.NEWPAGE;
        state.documents.peek().add(controlElement);
    }
}
