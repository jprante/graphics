package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;

/**
 * A helper class that supports the scanline provider in navigating the structure of a Java image.
 */
final class ScanlineCursor {

    final int scanlineStride;

    final int maxPosition;

    int position;

    public ScanlineCursor(Raster raster) {
        // the data buffer can have lines that are longer than width * bytes per pixel, can have
        // extra at the end
        this.scanlineStride = getScanlineStride(raster);
        // the data buffer itself could be longer
        this.position = raster.getDataBuffer().getOffset();
        this.maxPosition = raster.getDataBuffer().getSize();
    }

    /**
     * Returns the initial position of the current line, and moves to the next.
     */
    public int next() {
        final int result = position;
        if (result >= maxPosition) {
            throw new IllegalStateException(
                    "We got past the end of the buffer, current position is " + position
                            + " and max position value is " + maxPosition);
        }
        position += scanlineStride;
        return result;
    }

    /**
     * Gets the scanline stride for the given raster.
     */
    int getScanlineStride(Raster raster) {
        if (raster.getSampleModel() instanceof ComponentSampleModel) {
            ComponentSampleModel csm = ((ComponentSampleModel) raster.getSampleModel());
            return csm.getScanlineStride();
        } else {
            return raster.getDataBuffer().getSize() / raster.getHeight();
        }
    }

}
