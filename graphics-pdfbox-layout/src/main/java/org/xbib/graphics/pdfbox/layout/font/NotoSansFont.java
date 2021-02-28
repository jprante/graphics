package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.IOException;
import java.util.Objects;

public class NotoSansFont implements Font {

    private static PDType0Font regular;

    private static PDType0Font bold;

    private static PDType0Font italic;

    private static PDType0Font bolditalic;

    public NotoSansFont(PDDocument document) throws IOException {
        if (regular == null) {
            regular = PDType0Font.load(document, Objects.requireNonNull(getClass().getResourceAsStream("NotoSans-Regular.ttf")));
            bold =  PDType0Font.load(document, Objects.requireNonNull(getClass().getResourceAsStream("NotoSans-Bold.ttf")));
            italic =  PDType0Font.load(document, Objects.requireNonNull(getClass().getResourceAsStream("NotoSans-Italic.ttf")));
            bolditalic =  PDType0Font.load(document, Objects.requireNonNull(getClass().getResourceAsStream("NotoSans-BoldItalic.ttf")));
        }
    }

    @Override
    public PDFont getPlainFont() {
        return regular;
    }

    @Override
    public PDFont getBoldFont() {
        return bold;
    }

    @Override
    public PDFont getItalicFont() {
        return italic;
    }

    @Override
    public PDFont getBoldItalicFont() {
        return bolditalic;
    }
}
