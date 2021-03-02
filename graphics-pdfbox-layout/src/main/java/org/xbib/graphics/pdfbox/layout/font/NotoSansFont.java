package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class NotoSansFont implements Font {

    private final PDDocument document;

    private static PDType0Font regular;

    private static PDType0Font bold;

    private static PDType0Font italic;

    private static PDType0Font bolditalic;

    public NotoSansFont(PDDocument document) {
        this.document = document;
    }

    @Override
    public PDFont getRegularFont() {
        if (regular == null) {
           regular = load(document, "NotoSans-Regular.ttf");
        }
        return regular;
    }

    @Override
    public PDFont getBoldFont() {
        if (bold == null) {
            bold = load(document, "NotoSans-Bold.ttf");
        }
        return bold;
    }

    @Override
    public PDFont getItalicFont() {
        if (italic == null) {
            italic = load(document, "NotoSans-Italic.ttf");
        }
        return italic;
    }

    @Override
    public PDFont getBoldItalicFont() {
        if (bolditalic == null) {
            bolditalic = load(document, "NotoSans-BoldItalic.ttf");
        }
        return bolditalic;
    }

    private static PDType0Font load(PDDocument document, String resourceName) {
        try {
            return PDType0Font.load(document, Objects.requireNonNull(NotoSansFont.class.getResourceAsStream(resourceName)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
