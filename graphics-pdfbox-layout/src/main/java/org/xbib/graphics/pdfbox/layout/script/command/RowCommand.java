package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.graphics.pdfbox.layout.table.Row;
import org.xbib.settings.Settings;

import java.io.IOException;

public class RowCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        Row.Builder row = Row.builder();
        row.padding(settings.getAsFloat("padding", 0f));
        state.elements.push(row);
        engine.executeElements(settings);
        state.elements.pop();
        state.elements.peek().add(row);
    }
}
