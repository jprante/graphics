package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.PositionControl;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class ResetpositionCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PositionControl.ResetPosition resetPosition = PositionControl.createResetPosition();
        state.elements.peek().add(resetPosition);
    }
}
