package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Paint;

public class SetPaintCommand extends StateCommand<Paint> {
    public SetPaintCommand(Paint paint) {
        super(paint);
    }
}

