package org.xbib.graphics.io.vector.commands;

import java.awt.Color;

public class SetXORModeCommand extends StateCommand<Color> {
    public SetXORModeCommand(Color mode) {
        super(mode);
    }
}

