package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Color;

public class SetXORModeCommand extends StateCommand<Color> {
    public SetXORModeCommand(Color mode) {
        super(mode);
    }
}

