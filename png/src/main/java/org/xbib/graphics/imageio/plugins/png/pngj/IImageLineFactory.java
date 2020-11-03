package org.xbib.graphics.imageio.plugins.png.pngj;

/**
 * Image Line factory.
 */
public interface IImageLineFactory<T extends IImageLine> {
    T createImageLine(ImageInfo iminfo);
}
