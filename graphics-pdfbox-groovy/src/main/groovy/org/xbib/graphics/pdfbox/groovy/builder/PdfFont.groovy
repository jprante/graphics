package org.xbib.graphics.pdfbox.groovy.builder

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.xbib.graphics.pdfbox.groovy.Font

class PdfFont {

    private static final DEFAULT_FONT = PDType1Font.HELVETICA

    private static fonts = [
            'Times-Roman': [regular: PDType1Font.TIMES_ROMAN,
                            bold: PDType1Font.TIMES_BOLD,
                            italic : PDType1Font.TIMES_ITALIC,
                            boldItalic: PDType1Font.TIMES_BOLD_ITALIC],
            'Helvetica'  : [regular: PDType1Font.HELVETICA,
                            bold: PDType1Font.HELVETICA_BOLD,
                            italic : PDType1Font.HELVETICA_OBLIQUE,
                            boldItalic: PDType1Font.HELVETICA_BOLD_OBLIQUE],
            'Courier'    : [regular: PDType1Font.COURIER,
                            bold: PDType1Font.COURIER_BOLD,
                            italic : PDType1Font.COURIER_OBLIQUE,
                            boldItalic: PDType1Font.COURIER_BOLD_OBLIQUE],
            'Symbol'     : [regular: PDType1Font.SYMBOL],
            'Dingbat'    : [regular: PDType1Font.ZAPF_DINGBATS]
    ]

    static boolean addFont(PDDocument document, String name, InputStream inputStream, boolean bold, boolean italic) {
        if (inputStream != null) {
            PDType0Font font = PDType0Font.load(document, inputStream)
            String fontName = name ?: font.baseFont
            fonts[fontName] = fonts[fontName] ?: [:]
            if (bold && italic) {
                fonts[fontName].boldItalic = font
            } else if (bold) {
                fonts[fontName].bold = font
            } else if (italic) {
                fonts[fontName].italic = font
            } else {
                fonts[fontName].regular = font
            }
            fonts[fontName].regular = fonts[fontName].regular ?: font
            true
        } else {
            false
        }
    }

    static PDFont getFont(Font font) {
        if (!font?.family || !fonts.containsKey(font.family)) {
            return DEFAULT_FONT
        }
        def fontOptions = fonts[font.family]
        PDFont pdfFont = fontOptions.containsKey('regular') ? fontOptions.regular : DEFAULT_FONT
        if (fontOptions) {
            if (font.italic && font.bold) {
                pdfFont = fontOptions.containsKey('boldItalic') ? fontOptions.boldItalic : pdfFont
            } else if (font.italic) {
                pdfFont = fontOptions.containsKey('italic') ? fontOptions.italic : pdfFont
            } else if (font.bold) {
                pdfFont = fontOptions.containsKey('bold') ? fontOptions.bold : pdfFont
            }
        }
        pdfFont
    }

    static boolean canEncode(Font font, String string) {
        canEncode(getFont(font), string)
    }

    static boolean canEncode(PDFont font, String string) {
        try {
            font.encode(string)
            return true
        } catch (Exception e) {
            return false
        }
    }
}
