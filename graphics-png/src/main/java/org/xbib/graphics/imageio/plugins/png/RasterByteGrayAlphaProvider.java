package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

/**
 * A scanline provider optimized for Raster objects containing a 8bit gray and alpha bands
 */
public final class RasterByteGrayAlphaProvider extends AbstractScanlineProvider {

    final static int[] PIXEL_STRIDES = new int[]{2};

    final byte[] bytes;

    boolean alphaFirst;

    int[] bandOffsets;

    int pixelStride;

    int numBands;

    public RasterByteGrayAlphaProvider(Raster raster) {
        super(raster, 8, raster.getWidth() * computePixelStride(raster, PIXEL_STRIDES));
        this.bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
        ComponentSampleModel sm = (ComponentSampleModel) raster.getSampleModel();
        this.bandOffsets = sm.getBandOffsets();
        this.numBands = sm.getNumBands();
        this.pixelStride = sm.getPixelStride();
        this.alphaFirst = bandOffsets[0] != 0;
    }

    @Override
    public void next(final byte[] row, final int offset, final int length) {
        int bytesIdx = cursor.next();
        if (!alphaFirst && (numBands == pixelStride)) {
            System.arraycopy(bytes, bytesIdx, row, offset, length);
        } else {
            int i = offset;
            final int max = offset + length;
            while (i < max) {
                for (int j = 0; j < numBands; j++) {
                    row[i + j] = bytes[bytesIdx + bandOffsets[j]];
                }
                bytesIdx += pixelStride;
                i += numBands;
            }
        }
    }
}
