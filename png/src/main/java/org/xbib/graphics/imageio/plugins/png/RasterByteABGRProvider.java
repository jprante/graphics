package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;

/**
 * A scanline provider optimized for Raster objects containing
 * a 8bit BGR or ABGR image.
 */
public final class RasterByteABGRProvider extends AbstractScanlineProvider {

    final static int[] PIXEL_STRIDES = new int[]{3,4};
    final byte[] bytes;
    final boolean bgrOrder;
    final boolean hasAlpha;
    final int pixelStride;
    final int[] bandOffsets;
    final int numBands;

    public RasterByteABGRProvider(Raster raster, boolean hasAlpha) {
        super(raster, 8, raster.getWidth() * (computePixelStride(raster, PIXEL_STRIDES, hasAlpha)));
        this.hasAlpha = hasAlpha;
        this.bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
        ComponentSampleModel sm = (ComponentSampleModel) raster.getSampleModel();
        this.bgrOrder = sm.getBandOffsets()[0] != 0;
        this.pixelStride = sm.getPixelStride();
        this.bandOffsets = sm.getBandOffsets();
        this.numBands = sm.getNumBands();
    }

    @Override
    public void next(final byte[] row, final int offset, final int length) {
        int bytesIdx = cursor.next();
        int i = offset;
        final int max = offset + length;
        if (!bgrOrder && (numBands == pixelStride)) {
            System.arraycopy(bytes, bytesIdx, row, offset, length);
        } else {
            while (i < max) {
                for (int j = 0; j < numBands; j++) {
                    // We assign data pixels on the expected order
                    // So if bgrOrder (bandOffset is 2,1,0):
                    // row[i+2] = B
                    // row[i+1] = G
                    // row[i+0] = R
                    row[i + j] = bytes[bytesIdx + bandOffsets[j]];
                }
                // Pixel stride may be longer than numBands due to bandSelect 
                // sharing same dataBuffer of the original image
                bytesIdx += pixelStride; 
                i += numBands;
            }
        }
    }
}
