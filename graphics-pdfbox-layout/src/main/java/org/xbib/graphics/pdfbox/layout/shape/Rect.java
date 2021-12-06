package org.xbib.graphics.pdfbox.layout.shape;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.text.Position;
import java.io.IOException;

/**
 * A simple rectangular shape.
 */
public class Rect extends AbstractShape {

    @Override
    public void add(PDDocument pdDocument, PDPageContentStream contentStream,
                    Position upperLeft, float width, float height) throws IOException {
        contentStream.addRect(upperLeft.getX(), upperLeft.getY() - height, width, height);
    }
}
