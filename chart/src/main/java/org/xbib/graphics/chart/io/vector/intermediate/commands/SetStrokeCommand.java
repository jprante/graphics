package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Stroke;

public class SetStrokeCommand extends StateCommand<Stroke> {
    public SetStrokeCommand(Stroke stroke) {
        super(stroke);
    }
}

