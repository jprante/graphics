package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

public class PDFGraphics2DProvider implements VectorGraphics2DProvider<PDFGraphics2D> {
    @Override
    public String name() {
        return "pdf";
    }

    @Override
    public PDFGraphics2D provide(double x, double y, double width, double height) {
        return new PDFGraphics2D(x, y, width, height);
    }
}
