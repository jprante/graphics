package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.VectorGraphics2DProvider;

import java.awt.Rectangle;

public class PDFGraphics2DProvider implements VectorGraphics2DProvider<PDFGraphics2D> {

    @Override
    public String name() {
        return "pdf";
    }

    @Override
    public PDFGraphics2D provide(Rectangle rectangle) {
        return new PDFGraphics2D(rectangle);
    }
}
