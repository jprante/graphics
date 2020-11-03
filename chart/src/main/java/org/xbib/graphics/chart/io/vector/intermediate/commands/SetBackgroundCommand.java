package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Color;

public class SetBackgroundCommand extends StateCommand<Color> {
    public SetBackgroundCommand(Color color) {
        super(color);
    }
}

