package org.xbib.graphics.io.vector.commands;

import java.awt.geom.AffineTransform;

public abstract class AffineTransformCommand extends StateCommand<AffineTransform> {
    public AffineTransformCommand(AffineTransform transform) {
        super(transform);
    }
}

