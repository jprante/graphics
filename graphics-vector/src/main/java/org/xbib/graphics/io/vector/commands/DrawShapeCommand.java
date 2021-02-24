package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.Command;
import java.awt.Shape;

public class DrawShapeCommand extends Command<Shape> {

    public DrawShapeCommand(Shape shape) {
        super(clone(shape));
    }

    @Override
    public String getKey() {
        return "drawShape";
    }
}
