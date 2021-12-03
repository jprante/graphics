package org.xbib.graphics.pdfbox.layout.shape;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Path implements Shape {

    private final List<Position> list;

    public Path() {
        this.list = new ArrayList<>();
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
                     float width, float height, Color color, Stroke stroke, DrawListener drawListener) throws IOException {
        contentStream.saveGraphicsState();
        contentStream.moveTo(upperLeft.getX(), upperLeft.getY());
        contentStream.setStrokingColor(color);
        contentStream.setLineCapStyle(stroke.getCapStyle().value());
        contentStream.setLineDashPattern(stroke.getDashPattern().getPattern(), stroke.getDashPattern().getPhase());
        contentStream.setLineJoinStyle(stroke.getJoinStyle().value());
        contentStream.setLineWidth(stroke.getLineWidth());
        for (Position p : list) {
            contentStream.lineTo(p.getX(), p.getY());
        }
        contentStream.restoreGraphicsState();
        drawListener.drawn(this, upperLeft, width, height);
    }

    @Override
    public void fill(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
                     float width, float height, Color color, DrawListener drawListener) throws IOException {
        // do not fill
    }

    @Override
    public void add(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
                    float width, float height) throws IOException {
        list.add(new Position(width, height));
    }
}
