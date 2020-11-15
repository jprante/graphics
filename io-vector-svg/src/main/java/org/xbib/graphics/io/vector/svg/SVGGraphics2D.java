package org.xbib.graphics.io.vector.svg;

import org.xbib.graphics.io.vector.VectorGraphics2D;
import org.xbib.graphics.io.vector.PageSize;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Scaled Vector Graphics</i> (SVG) format.
 */
public class SVGGraphics2D extends VectorGraphics2D {

    public SVGGraphics2D(Rectangle rectangle) {
        super(new SVGProcessor(), new PageSize(rectangle));
        setColor(Color.BLACK);
    }
}
