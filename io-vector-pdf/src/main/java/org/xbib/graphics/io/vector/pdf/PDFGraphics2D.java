package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.VectorGraphics2D;
import org.xbib.graphics.io.vector.PageSize;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Portable Document Format</i> (PDF).
 */
public class PDFGraphics2D extends VectorGraphics2D {

    public PDFGraphics2D(Rectangle rectangle) {
        super(new PDFProcessor(), new PageSize(rectangle));
        setColor(Color.BLACK);
        setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, null, 0f));
    }
}
