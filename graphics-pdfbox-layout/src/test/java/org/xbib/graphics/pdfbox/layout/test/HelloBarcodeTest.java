package org.xbib.graphics.pdfbox.layout.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.barcode.Code3Of9;
import org.xbib.graphics.barcode.HumanReadableLocation;
import org.xbib.graphics.barcode.Symbol;
import org.xbib.graphics.pdfbox.layout.elements.BarcodeElement;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import org.xbib.graphics.pdfbox.layout.elements.PageFormats;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.text.Indent;
import org.xbib.graphics.pdfbox.layout.text.SpaceUnit;
import java.io.FileOutputStream;

public class HelloBarcodeTest {

    @Test
    public void test() throws Exception {
        Document document = new Document(PageFormats.A4_PORTRAIT);
        Paragraph paragraph = new Paragraph();
        paragraph.add(new Indent(50, SpaceUnit.pt));
        paragraph.addMarkup("Hello Barcode\n", 12, BaseFont.HELVETICA);
        document.add(paragraph);
        Symbol symbol = new Code3Of9();
        symbol.setContent("1234567890");
        symbol.setBarHeight(150);
        symbol.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
        BarcodeElement barcodeElement = new BarcodeElement(symbol);
        document.add(barcodeElement, new VerticalLayoutHint(Alignment.LEFT, 10, 10, 10, 10, true));
        document.render().save(new FileOutputStream("build/hellobarcode.pdf")).close();
    }
}
