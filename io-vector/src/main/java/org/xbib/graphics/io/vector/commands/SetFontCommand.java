package org.xbib.graphics.io.vector.commands;

import java.awt.Font;

public class SetFontCommand extends StateCommand<Font> {
    public SetFontCommand(Font font) {
        super(font);
    }
}

