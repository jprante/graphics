package org.xbib.graphics.pdfbox.layout.elements;

public interface PageFormats {

    PageFormat A4_PORTRAIT = new PageFormat();

    PageFormat A4_LANDSCAPE = new PageFormat(PageFormat.A4, Orientation.LANDSCAPE);

    PageFormat A5_PORTRAIT = new PageFormat(PageFormat.A5, Orientation.PORTRAIT);

    PageFormat A5_LANDSCAPE = new PageFormat(PageFormat.A5, Orientation.LANDSCAPE);
}
