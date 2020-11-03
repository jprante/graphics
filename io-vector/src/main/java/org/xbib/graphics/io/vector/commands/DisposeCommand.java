package org.xbib.graphics.io.vector.commands;

import org.xbib.graphics.io.vector.VectorGraphics2D;

public class DisposeCommand extends StateCommand<VectorGraphics2D> {
    public DisposeCommand(VectorGraphics2D graphics) {
        super(graphics);
    }
}

