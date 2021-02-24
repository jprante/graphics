package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;

/**
 * A scanline provider that can copy 1-1 data from the buffered image into the scanline without
 * performing any kind of transformation.
 */
public final class RasterByteSingleBandProvider extends AbstractScanlineProvider {

    final byte[] bytes;

    public RasterByteSingleBandProvider(Raster raster, int bitDepth, int scanlineLength) {
        super(raster, bitDepth, scanlineLength);
        this.bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
    }

    public RasterByteSingleBandProvider(Raster raster, int bitDepth, int scanlineLength,
            IndexColorModel palette) {
        super(raster, bitDepth, scanlineLength, palette);
        this.bytes = ((DataBufferByte) raster.getDataBuffer()).getData();
    }

    @Override
    public void next(final byte[] scanline, final int offset, final int length) {
        if (this.currentRow == height) {
            throw new IllegalStateException("All scanlines have been read already");
        }
        final int next = cursor.next();
        System.arraycopy(bytes, next, scanline, offset, length);
        currentRow++;
    }
}
