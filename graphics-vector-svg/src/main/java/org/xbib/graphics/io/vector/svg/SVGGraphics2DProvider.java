package org.xbib.graphics.io.vector.svg;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

import java.awt.Rectangle;

public class SVGGraphics2DProvider implements VectorGraphics2DProvider<SVGGraphics2D> {

    @Override
    public String name() {
        return "eps";
    }

    @Override
    public SVGGraphics2D provide(Rectangle rectangle) {
        return new SVGGraphics2D(rectangle);
    }
}
