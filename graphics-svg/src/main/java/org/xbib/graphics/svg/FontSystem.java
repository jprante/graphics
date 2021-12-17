package org.xbib.graphics.svg;

import org.xbib.graphics.svg.element.Font;
import org.xbib.graphics.svg.element.glyph.Glyph;
import org.xbib.graphics.svg.element.glyph.MissingGlyph;

import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class FontSystem extends Font {

    java.awt.Font sysFont;

    Map<String, Glyph> glyphCache = new HashMap<>();

    static Set<String> sysFontNames = new HashSet<>();

    public static boolean checkIfSystemFontExists(String fontName) {
        if (sysFontNames.isEmpty()) {
            Collections.addAll(sysFontNames, GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(Locale.ENGLISH));
        }

        return sysFontNames.contains(fontName);
    }

    public static FontSystem createFont(String[] fontFamilies, int fontStyle, int fontWeight, float fontSize) {
        for (String fontName : fontFamilies) {
            String javaFontName = mapJavaFontName(fontName);
            if (checkIfSystemFontExists(javaFontName)) {
                return new FontSystem(javaFontName, fontStyle, fontWeight, fontSize);
            }
        }

        return null;
    }

    private static String mapJavaFontName(String fontName) {
        if ("serif".equals(fontName)) {
            return java.awt.Font.SERIF;
        } else if ("sans-serif".equals(fontName)) {
            return java.awt.Font.SANS_SERIF;
        } else if ("monospace".equals(fontName)) {
            return java.awt.Font.MONOSPACED;
        }
        return fontName;
    }

    private FontSystem(String fontFamily, int fontStyle, int fontWeight, float fontSize) {
        int style;
        if (fontStyle == Text.TXST_ITALIC) {
            style = java.awt.Font.ITALIC;
        } else {
            style = java.awt.Font.PLAIN;
        }
        int weight;
        switch (fontWeight) {
            case Text.TXWE_BOLD:
            case Text.TXWE_BOLDER:
                weight = java.awt.Font.BOLD;
                break;
            default:
                weight = java.awt.Font.PLAIN;
                break;
        }
        sysFont = new java.awt.Font(fontFamily, style | weight, 1).deriveFont(fontSize);
        FontRenderContext fontRenderContext = new FontRenderContext(null, true, true);
        LineMetrics lineMetrics = sysFont.getLineMetrics("M", fontRenderContext);
        FontFace face = new FontFace();
        face.setAscent((int) lineMetrics.getAscent());
        face.setDescent((int) lineMetrics.getDescent());
        face.setUnitsPerEm((int) sysFont.getStringBounds("M", fontRenderContext).getWidth());
        setFontFace(face);
    }

    @Override
    public MissingGlyph getGlyph(String unicode) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        GlyphVector vec = sysFont.createGlyphVector(frc, unicode);
        Glyph glyph = glyphCache.get(unicode);
        if (glyph == null) {
            glyph = new Glyph();
            glyph.setPath(vec.getGlyphOutline(0));
            GlyphMetrics gm = vec.getGlyphMetrics(0);
            glyph.setHorizAdvX(gm.getAdvanceX());
            glyph.setVertAdvY(gm.getAdvanceY());
            glyph.setVertOriginX(0);
            glyph.setVertOriginY(0);
            glyphCache.put(unicode, glyph);
        }
        return glyph;
    }
}
