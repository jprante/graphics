package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.PositionControl;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class MarkpositionCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PositionControl.MarkPosition markPosition = PositionControl.createMarkPosition();
        state.elements.peek().add(markPosition);
    }
}
