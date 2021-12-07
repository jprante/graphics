package org.xbib.graphics.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.table.BorderStyleInterface;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.Row;
import org.xbib.graphics.pdfbox.layout.table.Table;
import org.xbib.graphics.pdfbox.layout.table.TableRenderer;
import org.xbib.graphics.pdfbox.layout.table.VerticalAlignment;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;

import java.awt.Color;
import java.io.IOException;

public class TableElement implements Element, Drawable, Dividable {

    private final Table.Builder table;

    private Position absolutePosition;

    public TableElement() {
        this.table = Table.builder();
    }

    @Override
    public Element add(Element element) {
        if (element instanceof Row.Builder) {
            Row row = ((Row.Builder) element).build();
            table.addRow(row);
        } else {
            throw new UnsupportedOperationException();
        }
        return this;
    }

    public void addColumnOfWidth(float width) {
        table.addColumnOfWidth(width);
    }

    public void padding(float padding) {
        table.padding(padding);
    }

    public void textColor(Color color) {
        table.textColor(color);
    }

    public void backgroundColor(Color color) {
        table.backgroundColor(color);
    }

    public void borderColor(Color color) {
        table.borderColor(color);
    }

    public void borderWidth(float borderWidth) {
        table.borderWidth(borderWidth);
    }

    public void borderStyle(BorderStyleInterface style) {
        table.borderStyle(style);
    }

    public void horizontalAlignment(HorizontalAlignment horizontalAlignment) {
        table.horizontalAlignment(horizontalAlignment);
    }

    public void verticalAlignment(VerticalAlignment verticalAlignment) {
        table.verticalAlignment(verticalAlignment);
    }

    @Override
    public float getWidth() {
       return table.build().getWidth();
    }

    @Override
    public float getHeight() {
        return table.build().getHeight();
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight) throws IOException {
        if (getHeight() <= nextPageHeight) {
            return new Divided(new VerticalSpacer(remainingHeight), this);
        }
        return new Cutter(this).divide(remainingHeight, nextPageHeight);
    }

    @Override
    public Position getAbsolutePosition() {
        return absolutePosition;
    }

    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
                     Position upperLeft, DrawListener drawListener) throws IOException {
        TableRenderer tableRenderer = TableRenderer.builder()
                .table(table.build())
                .document(pdDocument)
                .contentStream(contentStream)
                .startX(upperLeft.getX())
                .startY(upperLeft.getY())
                .build();
        tableRenderer.draw();
        if (drawListener != null) {
            drawListener.drawn(this, upperLeft, getWidth(), getHeight());
        }
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() {
        return this;
    }
}
