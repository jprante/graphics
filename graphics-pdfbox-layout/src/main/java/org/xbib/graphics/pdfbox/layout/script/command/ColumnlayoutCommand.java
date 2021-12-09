package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.elements.render.ColumnLayout;
import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;

public class ColumnlayoutCommand implements Command {

    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        ColumnLayout columnLayout = new ColumnLayout();
        columnLayout.setColumnCount(settings.getAsInt("columns", 2));
        columnLayout.setColumnSpacing(settings.getAsFloat("spacing", 10f));
        state.elements.peek().add(columnLayout);
    }
}
