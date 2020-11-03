package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Color;

public class SetColorCommand extends StateCommand<Color> {
    public SetColorCommand(Color color) {
        super(color);
    }
}

