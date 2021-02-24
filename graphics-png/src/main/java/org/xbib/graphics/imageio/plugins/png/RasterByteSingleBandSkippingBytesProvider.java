package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;

/**
 * A scanline provider that copy data from the buffered image into the scanline 
 * by skipping some bytes due to pixelStride not equal to number of bands
 * (There might be some bandSelect happening).
 */
public final class RasterByteSingleBandSkippingBytesProvider extends AbstractScanlineProvider {

    final static int[] PIXEL_STRIDES = new int[]{1};

    int pixelStride;

    final byte[] bytes;

    int[] bandOffsets;

    int numBands;

    public RasterByteSingleBandSkippingBytesProvider(Raster raster) {
        super(raster, 8, raster.getWidth() * computePixelStride(raster, PIXEL_STRIDES));
        PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel) raster.getSampleModel();
        this.bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
        this.pixelStride = sm.getPixelStride();
        this.numBands = sm.getNumBands();
        this.bandOffsets = sm.getBandOffsets();
    }

    @Override
    public void next(final byte[] scanline, final int offset, final int length) {
        if (this.currentRow == height) {
            throw new IllegalStateException("All scanlines have been read already");
        }
        int bytesIdx = cursor.next();
        if (numBands == pixelStride) {
            System.arraycopy(bytes, bytesIdx, scanline, offset, length);
        } else {
            int i = offset;
            final int max = offset + length;
            while (i < max) {
                for (int j = 0; j < numBands; j++) {
                    scanline[i + j] = bytes[bytesIdx + bandOffsets[j]];
                }
                bytesIdx += pixelStride;
                i += numBands;
            }
        }
        currentRow++;
    }
}
