package org.xbib.graphics.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.chart.QuickChart;
import org.xbib.graphics.chart.xy.XYChart;
import org.xbib.graphics.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.pdfbox.layout.text.DrawListener;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.WidthRespecting;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

public class ChartElement implements Element, Drawable, Dividable, WidthRespecting {

    String chartTitle;

    private double[] xData;

    private double[] yData;

    private float width;

    private float height;

    private float scaleX = 1.0f;

    private float scaleY = 1.0f;

    private float maxWidth = -1;

    private Position absolutePosition;

    public void setXData(double[] xData) {
        this.xData = xData;
    }

    public void setYData(double[] yData) {
        this.yData = yData;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getWidth() throws IOException {
        return width * scaleX;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getHeight() throws IOException {
        return height * scaleY;
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

    public void setAbsolutePosition(Position absolutePosition) {
        this.absolutePosition = absolutePosition;
    }

    @Override
    public void draw(PDDocument pdDocument,
                     PDPageContentStream contentStream,
                     Position upperLeft,
                     DrawListener drawListener) throws IOException {
        float x = upperLeft.getX();
        float y = upperLeft.getY() - getHeight();
        PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(pdDocument, getWidth(), getHeight());
        XYChart chart = QuickChart.getChart("Hello Jörg",
                "X", "Y", "y(x)", xData, yData, (int) getWidth(), (int) getHeight());
        chart.paint(pdfBoxGraphics2D, (int) getWidth(), (int) getHeight());
        PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject();
        xFormObject.setMatrix(AffineTransform.getTranslateInstance(x, y));
        Matrix matrix = new Matrix();
        matrix.translate(x, y);
        matrix.scale(scaleX, scaleY);
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
