package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import java.io.IOException;

/**
 * Unit to specify space, currently only em and pt.
 */
public enum SpaceUnit {

    /**
     * The average character width of the associated font.
     */
    em,
    /**
     * Measuring in points.
     */
    pt;

    /**
     * Converts the given unit to pt.
     *
     * @param size           the size with respect to the unit.
     * @param fontDescriptor the font/size to use.
     * @return the size in pt.
     * @throws IOException by pdfbox
     */
    public float toPt(float size, FontDescriptor fontDescriptor) throws IOException {
        if (this == em) {
            return fontDescriptor.getSize() * fontDescriptor.getSelectedFont().getAverageFontWidth() / 1000 * size;
        }
        return size;
    }
}
