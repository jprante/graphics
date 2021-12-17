package org.xbib.graphics.svg;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;

public class AdobeComposite implements Composite {

    public static final int CT_NORMAL = 0;

    public static final int CT_MULTIPLY = 1;

    public static final int CT_LAST = 2;

    final int compositeType;

    final float extraAlpha;

    public AdobeComposite(int compositeType, float extraAlpha) {
        this.compositeType = compositeType;
        this.extraAlpha = extraAlpha;
        if (compositeType < 0 || compositeType >= CT_LAST) {
            throw new IllegalArgumentException("Invalid composite type");
        }
        if (extraAlpha < 0f || extraAlpha > 1f) {
            throw new IllegalArgumentException("Invalid alpha");
        }
    }

    public int getCompositeType() {
        return compositeType;
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new AdobeCompositeContext(compositeType, extraAlpha);
    }
}
