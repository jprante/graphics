package org.xbib.graphics.chart.io.vector.intermediate.commands;

import java.awt.geom.AffineTransform;

public class SetTransformCommand extends StateCommand<AffineTransform> {
    public SetTransformCommand(AffineTransform transform) {
        super(new AffineTransform(transform));
    }
}

