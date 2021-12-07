package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.PositionControl;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class SetpositionCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        PositionControl.SetPosition setPosition = PositionControl.createSetPosition(settings.getAsFloat("x", null), settings.getAsFloat("y", null));
        state.elements.peek().add(setPosition);
    }
}
