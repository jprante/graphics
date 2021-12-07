package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.ControlElement;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class NewcolumnCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ControlElement controlElement = ControlElement.NEWCOLUMN;
        state.elements.peek().add(controlElement);
    }
}
