package org.xbib.graphics.pdfbox.layout.elements;

public interface PageFormats {

    PageFormat A4_PORTRAIT = new PageFormat(PageFormat.A4, Orientation.PORTRAIT);

    PageFormat A4_LANDSCAPE = new PageFormat(PageFormat.A4, Orientation.LANDSCAPE);

    PageFormat A5_PORTRAIT = new PageFormat(PageFormat.A5, Orientation.PORTRAIT);

    PageFormat A5_LANDSCAPE = new PageFormat(PageFormat.A5, Orientation.LANDSCAPE);

    PageFormat LETTER_PORTRAIT = new PageFormat(PageFormat.Letter, Orientation.PORTRAIT);

    PageFormat LETTER_LANDSCAPE = new PageFormat(PageFormat.Letter, Orientation.LANDSCAPE);
}
