package org.xbib.graphics.pdfbox.layout.util;

import static org.xbib.graphics.pdfbox.layout.table.BorderStyle.SOLID;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.table.render.PositionedLine;
import org.xbib.graphics.pdfbox.layout.table.render.PositionedRectangle;
import org.xbib.graphics.pdfbox.layout.table.render.PositionedStyledText;

import java.awt.Color;
import java.io.IOException;
import java.io.UncheckedIOException;

public class RenderUtil {

    private RenderUtil() {
    }

    public static void drawText(PDPageContentStream contentStream, PositionedStyledText styledText) {
        try {
            contentStream.beginText();
            contentStream.setNonStrokingColor(styledText.getColor());
            // TODO select correct font
            contentStream.setFont(styledText.getFont().getRegularFont(), styledText.getFontSize());
            contentStream.newLineAtOffset(styledText.getX(), styledText.getY());
            contentStream.showText(styledText.getText());
            contentStream.endText();
            contentStream.setCharacterSpacing(0);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void drawLine(PDPageContentStream contentStream, PositionedLine line) {
        try {
            contentStream.moveTo(line.getStartX(), line.getStartY());
            contentStream.setLineWidth(line.getWidth());
            contentStream.lineTo(line.getEndX(), line.getEndY());
            contentStream.setStrokingColor(line.getColor());
            contentStream.setLineDashPattern(line.getBorderStyle().getPattern(), line.getBorderStyle().getPhase());
            contentStream.stroke();
            contentStream.setStrokingColor(line.getResetColor());
            contentStream.setLineDashPattern(SOLID.getPattern(), SOLID.getPhase());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void drawRectangle(PDPageContentStream contentStream, PositionedRectangle rectangle) {
        try {
            contentStream.setNonStrokingColor(rectangle.getColor());
            contentStream.addRect(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
