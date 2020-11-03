package org.xbib.graphics.chart.io.vector.intermediate.commands;

import org.xbib.graphics.chart.io.vector.util.GraphicsUtils;

import java.awt.Shape;

public class FillShapeCommand extends Command<Shape> {
    public FillShapeCommand(Shape shape) {
        super(GraphicsUtils.clone(shape));
    }
}

