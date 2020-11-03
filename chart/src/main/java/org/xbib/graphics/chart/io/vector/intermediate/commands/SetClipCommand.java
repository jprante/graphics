package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.Shape;

public class SetClipCommand extends StateCommand<Shape> {
    public SetClipCommand(Shape shape) {
        super(shape);
    }
}

