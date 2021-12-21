package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.element.glyph.Glyph;
import org.xbib.graphics.svg.element.glyph.MissingGlyph;
import org.xbib.graphics.svg.element.shape.Text;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.util.HashMap;
import java.util.Map;

public class FontSystem extends Font {

    private final java.awt.Font sysFont;

    private final Map<String, Glyph> glyphCache = new HashMap<>();

    public FontSystem(String fontFamily, int fontStyle, int fontWeight, float fontSize) {
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
