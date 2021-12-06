package org.xbib.graphics.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.barcode.Symbol;
import org.xbib.graphics.barcode.render.BarcodeGraphicsRenderer;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.WidthRespecting;
import java.awt.Color;
import java.io.IOException;

public class BarcodeElement implements Element, Drawable, Dividable, WidthRespecting {

    private final Symbol symbol;

    private float width;

    private float height;

    private float scale;

    private float maxWidth = -1;

    private Position absolutePosition;

    public BarcodeElement(Symbol symbol) {
        this.symbol = symbol;
        setWidth(symbol.getWidth());
        setHeight(symbol.getHeight());
        setScale(1.0f);
    }

    public void setScale(float scale) {
        this.scale = scale;
        setWidth(width * scale);
        setHeight(height * scale);
    }

    public float getScale() {
        return scale;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getWidth() throws IOException {
        return width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getHeight() throws IOException {
        return height;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
            throws IOException {
        if (getHeight() <= nextPageHeight) {
            return new Divided(new VerticalSpacer(remainingHeight), this);
        }
        return new Cutter(this).divide(remainingHeight, nextPageHeight);
    }

    @Override
    public float getMaxWidth() {
        return maxWidth;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
    }

    @Override
    public Position getAbsolutePosition() {
        return absolutePosition;
    }

    /**
     * Sets the absolute position to render at.
     *
     * @param absolutePosition the absolute position.
     */
    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) throws IOException {
        float x = upperLeft.getX();
        float y = upperLeft.getY() - height;
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(pdDocument, width, height);
        BarcodeGraphicsRenderer renderer = new BarcodeGraphicsRenderer(pdfBoxGraphics2D, null, 1.0d,
                Color.WHITE, Color.BLACK, false, false);
        renderer.render(symbol);
        renderer.close();
        PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject();
        Matrix matrix = new Matrix();
        matrix.translate(x, y);
        contentStream.saveGraphicsState();
        contentStream.transform(matrix);
        contentStream.drawForm(xFormObject);
        contentStream.restoreGraphicsState();
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }
}
