package org.xbib.graphics.io.vector.svg;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

public class SVGGraphics2DProvider implements VectorGraphics2DProvider<SVGGraphics2D> {
    @Override
    public String name() {
        return "eps";
    }

    @Override
    public SVGGraphics2D provide(double x, double y, double width, double height) {
        return new SVGGraphics2D(x, y, width, height);
    }
}
