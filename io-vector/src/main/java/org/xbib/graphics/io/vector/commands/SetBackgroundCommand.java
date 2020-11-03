package org.xbib.graphics.io.vector.commands;

import java.awt.Color;

public class SetBackgroundCommand extends StateCommand<Color> {
    public SetBackgroundCommand(Color color) {
        super(color);
    }
}

