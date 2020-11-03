package org.xbib.graphics.io.vector.commands;

import java.awt.Shape;

public class SetClipCommand extends StateCommand<Shape> {
    public SetClipCommand(Shape shape) {
        super(shape);
    }
}

