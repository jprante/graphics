package org.xbib.graphics.pdfbox.layout.boxable;

class FontMetrics {

    private final float ascent;

    private final float descent;

    private final float height;

    public FontMetrics(final float height, final float ascent, final float descent) {
        this.height = height;
        this.ascent = ascent;
        this.descent = descent;
    }

    public float getAscent() {
        return ascent;
    }

    public float getDescent() {
        return descent;
    }

    public float getHeight() {
        return height;
    }
}
