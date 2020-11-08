package org.xbib.graphics.io.vector.svg;

import org.xbib.graphics.io.vector.VectorGraphics2D;
import org.xbib.graphics.io.vector.PageSize;
import java.awt.Color;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Scaled Vector Graphics</i> (SVG) format.
 */
public class SVGGraphics2D extends VectorGraphics2D {

    /**
     * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
     * commands to SVG data. The document dimensions must be specified as
     * parameters.
     *
     * @param x      Left offset.
     * @param y      Top offset
     * @param width  Width.
     * @param height Height.
     */
    public SVGGraphics2D(double x, double y, double width, double height) {
        super(new SVGProcessor(), new PageSize(x, y, width, height));
        setColor(Color.BLACK);
    }
}
