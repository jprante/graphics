package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.PositionControl;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class MovepositionCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PositionControl.MovePosition movePosition = PositionControl.createMovePosition(settings.getAsFloat("x", null), settings.getAsFloat("y", null));
        state.documents.peek().add(movePosition);
    }
}
