package org.xbib.graphics.pdfbox.layout.element;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.shape.Path;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;

import java.awt.Color;
import java.io.IOException;

public class PathElement implements Drawable, Element {

    private final Path path;

    private final Stroke stroke;

    private final Color color;

    private final Position position;

    private float width;

    private float height;

    public PathElement(Path path, Stroke stroke, Color color, Position position) {
        this.path = path;
        this.stroke = stroke;
        this.color = color;
        this.position = position;
        setWidth(0f);
    }

    public Stroke getStroke() {
        return stroke;
    }

    public Color getColor() {
        return color;
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
    public Position getAbsolutePosition() {
        return position;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) throws IOException {
        path.draw(pdDocument, contentStream, upperLeft, getWidth(), getHeight(), color, stroke, drawListener);
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }
}
