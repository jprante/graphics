package org.xbib.graphics.imageio.plugins.png.pngj;

import java.io.OutputStream;

public interface IPngWriterFactory {
    PngWriter createPngWriter(OutputStream outputStream, ImageInfo imgInfo);
}
