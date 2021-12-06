package org.xbib.graphics.pdfbox.layout.shape;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

public class Path implements Shape {

    private final List<Position> list;

    public Path(List<Position> list) {
        this.list = list;
    }

    @Override
    public void draw(PDDocument pdDocument,
                     PDPageContentStream contentStream,
                     Position upperLeft,
                     float width,
                     float height,
                     Color color,
                     Stroke stroke,
                     DrawListener drawListener) throws IOException {
        contentStream.saveGraphicsState();
        float x = upperLeft.getX();
        float y = upperLeft.getY() - stroke.getLineWidth() / 2;
        contentStream.setStrokingColor(color);
        stroke.applyTo(contentStream);
        boolean move = true;
        for (Position p : list) {
            if (move) {
                contentStream.moveTo(x + p.getX(), y + p.getY());
                move = false;
            } else {
                contentStream.lineTo(x + p.getX(), y + p.getY());
            }
        }
        contentStream.stroke();
        contentStream.restoreGraphicsState();
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, width, height);
        }
    }

    @Override
    public void fill(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
                     float width, float height, Color color, DrawListener drawListener) throws IOException {
        // do not fill
    }

    @Override
    public void add(PDDocument pdDocument, PDPageContentStream contentStream, Position upperLeft,
                    float width, float height) throws IOException {
    }
}
