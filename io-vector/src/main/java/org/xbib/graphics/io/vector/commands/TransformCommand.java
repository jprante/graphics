package org.xbib.graphics.io.vector.commands;

import java.awt.geom.AffineTransform;

public class TransformCommand extends AffineTransformCommand {
    private final AffineTransform transform;

    public TransformCommand(AffineTransform transform) {
        super(transform);
        this.transform = new AffineTransform(transform);
    }

    public AffineTransform getTransform() {
        return transform;
    }
}

