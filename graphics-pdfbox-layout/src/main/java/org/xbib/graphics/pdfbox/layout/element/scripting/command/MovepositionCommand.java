package org.xbib.graphics.pdfbox.layout.element.scripting.command;

import org.xbib.graphics.pdfbox.layout.element.PositionControl;
import org.xbib.graphics.pdfbox.layout.element.scripting.Engine;
import org.xbib.graphics.pdfbox.layout.element.scripting.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class MovepositionCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PositionControl.MovePosition movePosition = PositionControl.createMovePosition(settings.getAsFloat("x", null), settings.getAsFloat("y", null));
        state.elements.peek().add(movePosition);
    }
}
