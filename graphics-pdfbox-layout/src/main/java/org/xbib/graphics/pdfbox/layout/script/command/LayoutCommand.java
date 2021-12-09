package org.xbib.graphics.pdfbox.layout.script.command;

import org.xbib.graphics.pdfbox.layout.script.Engine;
import org.xbib.graphics.pdfbox.layout.script.State;
import org.xbib.settings.Settings;

import java.io.IOException;
import java.util.Locale;

public class LayoutCommand implements Command {
    @Override
    public void execute(Engine engine, State state, Settings settings) throws IOException {
        String layout = settings.get("layout", "vertical").toUpperCase(Locale.ROOT);

    }
}
