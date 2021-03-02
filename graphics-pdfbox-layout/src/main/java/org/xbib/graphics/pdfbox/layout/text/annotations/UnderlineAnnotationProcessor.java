package org.xbib.graphics.pdfbox.layout.text.annotations;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.shape.Stroke;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.StyledText;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotations.UnderlineAnnotation;
import java.awt.Color;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This annotation processor handles the {@link UnderlineAnnotation} and adds
 * the needed hyperlink metadata to the PDF document.
 */
public class UnderlineAnnotationProcessor implements AnnotationProcessor {

    private final List<Line> linesOnPage = new ArrayList<>();

    @Override
    public void annotatedObjectDrawn(Annotated drawnObject,
                                     DrawContext drawContext,
                                     Position upperLeft,
                                     float width,
                                     float height) {
        if (!(drawnObject instanceof StyledText)) {
            return;
        }
        StyledText drawnText = (StyledText) drawnObject;
        for (UnderlineAnnotation underlineAnnotation : drawnObject.getAnnotationsOfType(UnderlineAnnotation.class)) {
            float fontSize = drawnText.getFontDescriptor().getSize();
            float ascent = fontSize * drawnText.getFontDescriptor().getSelectedFont().getFontDescriptor().getAscent() / 1000;
            float baselineOffset = fontSize * underlineAnnotation.getBaselineOffsetScale();
            float thickness = (0.01f + fontSize * 0.05f) * underlineAnnotation.getLineWeight();
            Position start = new Position(upperLeft.getX(), upperLeft.getY() - ascent + baselineOffset);
            Position end = new Position(start.getX() + width, start.getY());
            Stroke stroke = Stroke.builder().lineWidth(thickness).build();
            Line line = new Line(start, end, stroke, drawnText.getColor());
            linesOnPage.add(line);
        }
    }

    @Override
    public void beforePage(DrawContext drawContext) {
        linesOnPage.clear();
    }

    @Override
    public void afterPage(DrawContext drawContext) {
        for (Line line : linesOnPage) {
            line.draw(drawContext.getCurrentPageContentStream());
        }
        linesOnPage.clear();
    }

    @Override
    public void afterRender(PDDocument document) {
        linesOnPage.clear();
    }

    private static class Line {

        private final Position start;

        private final Position end;

        private final Stroke stroke;

        private final Color color;

        public Line(Position start, Position end, Stroke stroke, Color color) {
            super();
            this.start = start;
            this.end = end;
            this.stroke = stroke;
            this.color = color;
        }

        public void draw(PDPageContentStream contentStream) {
            try {
                if (color != null) {
                    contentStream.setStrokingColor(color);
                }
                if (stroke != null) {
                    stroke.applyTo(contentStream);
                }
                contentStream.moveTo(start.getX(), start.getY());
                contentStream.lineTo(end.getX(), end.getY());
                contentStream.stroke();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        @Override
        public String toString() {
            return "Line{" +
                    "start=" + start +
                    ", end=" + end +
                    ", stroke=" + stroke +
                    ", color=" + color +
                    '}';
        }
    }
}
