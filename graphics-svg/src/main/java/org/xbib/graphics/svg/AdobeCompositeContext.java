package org.xbib.graphics.svg;

import java.awt.CompositeContext;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

public class AdobeCompositeContext implements CompositeContext {

    final int compositeType;

    final float extraAlpha;

    float[] rgba_src = new float[4];

    float[] rgba_dstIn = new float[4];

    float[] rgba_dstOut = new float[4];

    public AdobeCompositeContext(int compositeType, float extraAlpha) {
        this.compositeType = compositeType;
        this.extraAlpha = extraAlpha;
        rgba_dstOut[3] = 1f;
    }

    @Override
    public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
        int width = src.getWidth();
        int height = src.getHeight();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                src.getPixel(i, j, rgba_src);
                dstIn.getPixel(i, j, rgba_dstIn);
                if (rgba_src[3] == 0) {
                    continue;
                }
                float alpha = rgba_src[3];
                switch (compositeType) {
                    default:
                    case AdobeComposite.CT_NORMAL:
                        rgba_dstOut[0] = rgba_src[0] * alpha + rgba_dstIn[0] * (1f - alpha);
                        rgba_dstOut[1] = rgba_src[1] * alpha + rgba_dstIn[1] * (1f - alpha);
                        rgba_dstOut[2] = rgba_src[2] * alpha + rgba_dstIn[2] * (1f - alpha);
                        break;
                    case AdobeComposite.CT_MULTIPLY:
                        rgba_dstOut[0] = rgba_src[0] * rgba_dstIn[0] * alpha + rgba_dstIn[0] * (1f - alpha);
                        rgba_dstOut[1] = rgba_src[1] * rgba_dstIn[1] * alpha + rgba_dstIn[1] * (1f - alpha);
                        rgba_dstOut[2] = rgba_src[2] * rgba_dstIn[2] * alpha + rgba_dstIn[2] * (1f - alpha);
                        break;
                }
            }
        }
    }

    @Override
    public void dispose() {
    }
}
