package org.xbib.graphics.pdfbox.groovy.builder

import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.xbib.graphics.barcode.HumanReadableLocation
import org.xbib.graphics.barcode.Symbol
import org.xbib.graphics.barcode.util.Hexagon
import org.xbib.graphics.barcode.util.TextBox

import java.awt.geom.Rectangle2D

class PdfboxRenderer {

    private final PDPageContentStream contentStream

    private final float startX

    private final float startY

    private final double scalingFactor

    PdfboxRenderer(PDPageContentStream contentStream, float startX, float startY, double scalingfactor) {
        this.contentStream = contentStream
        this.startX = startX
        this.startY = startY
        this.scalingFactor = scalingfactor
    }

    void render(Symbol symbol) throws IOException {
        Integer marginX = (symbol.getQuietZoneHorizontal() * scalingFactor) as Integer
        Integer marginY = (symbol.getQuietZoneVertical() * scalingFactor) as Integer
        // rectangles
        for (Rectangle2D.Double rect : symbol.rectangles) {
            float x = startX + (rect.x * scalingFactor) + marginX as float
            float y = startY + (rect.y * scalingFactor) + marginY as float
            float w = rect.width * scalingFactor as float
            float h = rect.height * scalingFactor as float
            contentStream.addRect(x, y, w, h)
            contentStream.fill()
        }
        // human readable
        if (symbol.getHumanReadableLocation() != HumanReadableLocation.NONE) {
            for (TextBox text : symbol.texts) {
                float x = startX + (text.x * scalingFactor) + marginX as float
                float y = startY + (text.y * scalingFactor) + marginY as float
                contentStream.beginText()
                contentStream.moveTo(x, y)
                contentStream.showText(text.text)
                contentStream.endText()
            }
        }
        // hexagon
        for (Hexagon hexagon : symbol.hexagons) {
            for (int j = 0; j < 6; j++) {
                contentStream.moveTo(hexagon.pointX[j] as float, hexagon.pointY[j] as float)
                contentStream.lineTo(hexagon.pointX[j] as float, hexagon.pointY[j] as float)
                contentStream.closePath()
                contentStream.fill()
            }
        }
        // ellipsis
        for (int i = 0; i < symbol.target.size(); i++) {

            // TODO
        }
    }
}
