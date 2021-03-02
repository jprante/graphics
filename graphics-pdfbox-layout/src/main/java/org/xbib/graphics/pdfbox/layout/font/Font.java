package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.font.PDFont;

public interface Font {

    PDFont getRegularFont();

    PDFont getBoldFont();

    PDFont getItalicFont();

    PDFont getBoldItalicFont();
}