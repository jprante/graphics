package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.util.GraphicsUtils;
import java.awt.Shape;

public class DrawShapeCommand extends Command<Shape> {

    public DrawShapeCommand(Shape shape) {
        super(GraphicsUtils.clone(shape));
    }
}

