package org.xbib.graphics.io.vector.eps;

import org.xbib.graphics.io.vector.PageSize;
import org.xbib.graphics.io.vector.VectorGraphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * {@code Graphics2D} implementation that saves all operations to a string
 * in the <i>Encapsulated PostScript®</i> (EPS) format.
 */
public class EPSGraphics2D extends VectorGraphics2D {

    public EPSGraphics2D(Rectangle rectangle) {
        super(new EPSProcessor(), new PageSize(rectangle));
        /*
         * The following are the default settings for the graphics state in an EPS file.
		 * Although they currently appear in the document output, they do not have to be set explicitly.
		 */
        // TODO: Default graphics state does not need to be printed in the document
        setColor(Color.BLACK);
        setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, null, 0f));
    }
}
