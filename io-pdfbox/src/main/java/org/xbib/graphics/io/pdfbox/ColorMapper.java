package org.xbib.graphics.io.pdfbox;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

import java.awt.Color;

/**
 * Map Color to PDColor
 */
public interface ColorMapper {
    /**
     * Map the given Color to a PDColor
     *
     * @param contentStream the content stream
     * @param color         the color to map
     * @return the mapped color
     */
    PDColor mapColor(PDPageContentStream contentStream, Color color);
}
