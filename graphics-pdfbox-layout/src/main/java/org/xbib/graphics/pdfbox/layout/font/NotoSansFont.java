package org.xbib.graphics.pdfbox.layout.font;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.xbib.graphics.pdfbox.layout.elements.Document;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class NotoSansFont implements Font {

    private final PDDocument pdDocument;

    private PDType0Font regular;

    private PDType0Font bold;

    private PDType0Font italic;

    private PDType0Font bolditalic;

    public NotoSansFont(Document document) {
        this.pdDocument = document.getPdDocument();
    }

    @Override
    public PDFont getRegularFont() {
        if (regular == null) {
            regular = load("NotoSans-Regular.ttf");
        }
        return regular;
    }

    @Override
    public PDFont getBoldFont() {
        if (bold == null) {
            bold = load("NotoSans-Bold.ttf");
        }
        return bold;
    }

    @Override
    public PDFont getItalicFont() {
        if (italic == null) {
            italic = load("NotoSans-Italic.ttf");
        }
        return italic;
    }

    @Override
    public PDFont getBoldItalicFont() {
        if (bolditalic == null) {
            bolditalic = load("NotoSans-BoldItalic.ttf");
        }
        return bolditalic;
    }

    private PDType0Font load(String resourceName) {
        try {
            return PDType0Font.load(pdDocument, Objects.requireNonNull(NotoSansFont.class.getResourceAsStream(resourceName)));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
