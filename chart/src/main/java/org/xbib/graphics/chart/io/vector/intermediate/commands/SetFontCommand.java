package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Font;

public class SetFontCommand extends StateCommand<Font> {
    public SetFontCommand(Font font) {
        super(font);
    }
}

