package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.ComponentSampleModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;

public abstract class AbstractScanlineProvider implements ScanlineProvider {

    protected final int width;
    
    protected final int height;
    
    protected final int scanlineLength;

    protected final ScanlineCursor cursor;

    protected final IndexColorModel palette;
    
    protected final byte bitDepth;
    
    protected int currentRow = 0;

    public AbstractScanlineProvider(Raster raster, int bitDepth, int scanlineLength) {
        this(raster, (byte) bitDepth, scanlineLength, null);
    }
    
    public AbstractScanlineProvider(Raster raster, int bitDepth, int scanlineLength, IndexColorModel palette) {
        this(raster, (byte) bitDepth, scanlineLength, palette);
    }
    
    protected AbstractScanlineProvider(Raster raster, byte bitDepth, int scanlineLength, IndexColorModel palette) {
        this.width = raster.getWidth();
        this.height = raster.getHeight();
        this.bitDepth = bitDepth;
        this.palette = palette;
        this.cursor = new ScanlineCursor(raster);
        this.scanlineLength = scanlineLength;
    }

    @Override
    public final int getWidth() {
        return width;
    }

    @Override
    public final int getHeight() {
        return height;
    }

    @Override
    public final byte getBitDepth() {
        return bitDepth;
    }

    @Override
    public final IndexColorModel getPalette() {
        return palette;
    }

    @Override
    public final int getScanlineLength() {
        return scanlineLength;
    }

    @Override
    public void readFromPngRaw(byte[] raw, int len, int offset, int step) {
        throw new UnsupportedOperationException("This bridge works write only");
    }

    @Override
    public void endReadFromPngRaw() {
        throw new UnsupportedOperationException("This bridge works write only");
    }

    public void writeToPngRaw(byte[] raw) {
        this.next(raw, 1, raw.length - 1);
    }

    /**
     * Compute the pixelStride for the provided raster.
     * The actual raster pixelStride will be returned in case the raster number of bands
     * is not equal to the pixelStride.
     * Otherwise the expected pixelStride will be returned.
     * The expectedPixelStrides array can optionally have size = 2 (instead of 1).
     * The second value will be returned in case the raster has alpha.
     **/
    public static int computePixelStride(Raster raster, int[] expectedPixelStrides, boolean hasAlpha) {
        int pixelStride = ((ComponentSampleModel) raster.getSampleModel()).getPixelStride();
        if (raster.getNumBands() != pixelStride) {
            return pixelStride;
        }
        return (expectedPixelStrides.length == 2 && hasAlpha) ? expectedPixelStrides[1] : expectedPixelStrides[0];
    }

    /**
     * Compute the pixelStride for the provided raster.
     * The actual raster pixelStride will be returned in case the raster number of bands
     * is not equal to the pixelStride.
     * Otherwise the expected PixelStride will be returned assuming the raster has
     * no alpha.
     **/
    public static int computePixelStride(Raster raster, int[] expectedPixelStrides) {
        return computePixelStride(raster, expectedPixelStrides, false);
    }
}
