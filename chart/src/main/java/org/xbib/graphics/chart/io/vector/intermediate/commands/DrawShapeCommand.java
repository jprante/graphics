package org.xbib.graphics.chart.io.vector.intermediate.commands;

import org.xbib.graphics.chart.io.vector.util.GraphicsUtils;

import java.awt.Shape;

public class DrawShapeCommand extends Command<Shape> {
    public DrawShapeCommand(Shape shape) {
        super(GraphicsUtils.clone(shape));
    }
}

