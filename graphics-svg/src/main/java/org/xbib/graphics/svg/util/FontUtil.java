package org.xbib.graphics.svg.util;

import org.xbib.graphics.svg.element.Font;
import org.xbib.graphics.svg.FontSystem;
import org.xbib.graphics.svg.SVGDiagram;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.Text;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FontUtil {

    private static final String DEFAULT_FONT_FAMILY = "sans-serif";

    private static final float DEFAULT_FONT_SIZE = 12f;

    private static final int DEFAULT_LETTER_SPACING = 0;

    private static final int DEFAULT_FONT_STYLE = Text.TXST_NORMAL;

    private static final int DEFAULT_FONT_WEIGHT = Text.TXWE_NORMAL;

    private FontUtil() {
    }

    public final static class FontInfo {
        public final String[] families;
        public final float size;
        public final int style;
        public final int weight;
        public final float letterSpacing;

        public FontInfo(String[] families, float size, int style, int weight, float letterSpacing) {
            this.families = families;
            this.size = size;
            this.style = style;
            this.weight = weight;
            this.letterSpacing = letterSpacing;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FontInfo)) {
                return false;
            }
            FontInfo fontInfo = (FontInfo) o;
            return Float.compare(fontInfo.size, size) == 0
                    && style == fontInfo.style && weight == fontInfo.weight
                    && Float.compare(fontInfo.letterSpacing, letterSpacing) == 0
                    && Arrays.equals(families, fontInfo.families);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(size, style, weight, letterSpacing);
            result = 31 * result + Arrays.hashCode(families);
            return result;
        }
    }

    public static FontInfo parseFontInfo(SVGElement element, StyleAttribute sty) throws SVGException {
        String fontFamily = DEFAULT_FONT_FAMILY;
        if (element.getStyle(sty.setName("font-family"))) {
            fontFamily = sty.getStringValue();
        }
        float fontSize = DEFAULT_FONT_SIZE;
        if (element.getStyle(sty.setName("font-size"))) {
            fontSize = sty.getFloatValueWithUnits();
        }
        float letterSpacing = DEFAULT_LETTER_SPACING;
        if (element.getStyle(sty.setName("letter-spacing"))) {
            letterSpacing = sty.getFloatValueWithUnits();
        }
        int fontStyle = DEFAULT_FONT_STYLE;
        if (element.getStyle(sty.setName("font-style"))) {
            String s = sty.getStringValue();
            if ("normal".equals(s)) {
                fontStyle = Text.TXST_NORMAL;
            } else if ("italic".equals(s)) {
                fontStyle = Text.TXST_ITALIC;
            } else if ("oblique".equals(s)) {
                fontStyle = Text.TXST_OBLIQUE;
            }
        }
        int fontWeight = DEFAULT_FONT_WEIGHT;
        if (element.getStyle(sty.setName("font-weight"))) {
            String s = sty.getStringValue();
            if ("normal".equals(s)) {
                fontWeight = Text.TXWE_NORMAL;
            } else if ("bold".equals(s)) {
                fontWeight = Text.TXWE_BOLD;
            }
        }
        return new FontInfo(fontFamily.split(","), fontSize, fontStyle, fontWeight, letterSpacing);
    }

    public static Font getFont(FontInfo info, SVGDiagram diagram) {
        return getFont(info.families, info.style, info.weight, info.size, diagram);
    }

    private static Font getFont(String[] families, int fontStyle, int fontWeight, float fontSize, SVGDiagram diagram) {
        Font font = null;
        for (String family : families) {
            font = diagram.getUniverse().getFont(family);
            if (font != null) break;
        }
        if (font == null) {
            font = FontSystem.createFont(families, fontStyle, fontWeight, fontSize);
        }
        if (font == null) {
            Logger.getLogger(FontSystem.class.getName())
                    .log(Level.WARNING, "Could not create font " + Arrays.toString(families));
            String[] defaultFont = new String[]{FontUtil.DEFAULT_FONT_FAMILY};
            font = FontSystem.createFont(defaultFont, fontStyle, fontWeight, fontSize);
        }
        return font;
    }

}
