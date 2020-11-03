package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.IndexColorModel;

import org.xbib.graphics.imageio.plugins.png.pngj.IImageLine;

/**
 * The bridge between images and PNG scanlines 
 */
public interface ScanlineProvider extends IImageLine {

    /**
     * Image width
     */
    int getWidth();
    
    /**
     * Image height
     */
    int getHeight();
    
    /**
     * The bit depth of this image, 1, 2, 4, 8 or 16
     */
    byte getBitDepth();
    
    /**
     * The number of byte[] elements in the scaline
     */
    int getScanlineLength();
    
    /**
     * The next scanline, or throws an exception if we got past the end of the image
     */
    void next(byte[] scaline, int offset, int length);
    
    /**
     * Returns the palette for this image, or null if the image does not have one 
     */
    IndexColorModel getPalette();
}
