package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.Command;
import java.awt.Shape;

public class FillShapeCommand extends Command<Shape> {

    public FillShapeCommand(Shape shape) {
        super(clone(shape));
    }

    @Override
    public String getKey() {
        return "fillShape";
    }
}
