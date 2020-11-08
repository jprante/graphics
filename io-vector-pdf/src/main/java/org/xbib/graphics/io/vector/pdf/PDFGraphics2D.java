package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.VectorGraphics2D;
import org.xbib.graphics.io.vector.PageSize;
import java.awt.BasicStroke;
import java.awt.Color;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Portable Document Format</i> (PDF).
 */
public class PDFGraphics2D extends VectorGraphics2D {

    /**
     * Initializes a new VectorGraphics2D pipeline for translating Graphics2D
     * commands to PDF data. The document dimensions must be specified as
     * parameters.
     *
     * @param x      Left offset.
     * @param y      Top offset
     * @param width  Width.
     * @param height Height.
     */
    public PDFGraphics2D(double x, double y, double width, double height) {
        super(new PDFProcessor(), new PageSize(x, y, width, height));
        // TODO: Default graphics state does not need to be printed in the document
        setColor(Color.BLACK);
        setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, null, 0f));
    }
}
