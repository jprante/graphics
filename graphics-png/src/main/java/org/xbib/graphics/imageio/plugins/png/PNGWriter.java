package org.xbib.graphics.imageio.plugins.png;

import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;

import org.xbib.graphics.imageio.plugins.png.pngj.FilterType;
import org.xbib.graphics.imageio.plugins.png.pngj.ImageInfo;
import org.xbib.graphics.imageio.plugins.png.pngj.PngWriter;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.ChunksListForWrite;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.PngChunkPLTE;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.PngChunkTRNS;
import org.xbib.graphics.imageio.plugins.png.pngj.chunks.PngMetadata;

/**
 * Encodes a rednered image to PNG.
 */
public class PNGWriter {

    public void writePNG(RenderedImage image,
                         OutputStream outStream,
                         float quality,
                         FilterType filterType)  {
        writePNG(image, outStream, quality, filterType, null);
    }

    public void writePNG(RenderedImage image,
                         OutputStream outStream,
                         float quality,
                         FilterType filterType,
                         Map<String, String> text) {
        // compute the compression level similarly to what the Clib code does
        int level = Math.round(9 * (1f - quality));
        // get the optimal scanline provider for this image
        ScanlineProvider scanlines = ScanlineProviderFactory.getProvider(image);
        if (scanlines == null) {
            throw new IllegalArgumentException("Could not find a scanline extractor for " + image);
        }
        ColorModel colorModel = image.getColorModel();
        boolean indexed = colorModel instanceof IndexColorModel;
        ImageInfo ii = getImageInfo(image, scanlines, colorModel, indexed);
        try (PngWriter pw = new PngWriter(outStream, ii)) {
            pw.setShouldCloseStream(false);
            pw.setCompLevel(level);
            pw.setFilterType(filterType);
            ChunksListForWrite chunkList = pw.getChunksList();
            PngMetadata metadata = pw.getMetadata();
            if (indexed) {
                IndexColorModel icm = (IndexColorModel) colorModel;
                PngChunkPLTE palette = metadata.createPLTEChunk();
                int ncolors = icm.getMapSize();
                palette.setNentries(ncolors);
                for (int i = 0; i < ncolors; i++) {
                    final int red = icm.getRed(i);
                    final int green = icm.getGreen(i);
                    final int blue = icm.getBlue(i);
                    palette.setEntry(i, red, green, blue);
                }
                if (icm.hasAlpha()) {
                    PngChunkTRNS transparent = new PngChunkTRNS(ii);
                    int[] alpha = new int[ncolors];
                    for (int i = 0; i < ncolors; i++) {
                        final int a = icm.getAlpha(i);
                        alpha[i] = a;
                    }
                    transparent.setPalAlpha(alpha);
                    chunkList.queue(transparent);
                }
            }
            if (text != null && !text.isEmpty()) {
                for (Entry<String, String> entrySet : text.entrySet()) {
                    metadata.setText(entrySet.getKey(), entrySet.getValue(), true, false);
                }
            }
            // write out the actual image lines
            for (int row = 0; row < image.getHeight(); row++) {
                pw.writeRow(scanlines);
            }
            pw.end();
        }
    }

    /**
     * Quick method used for checking if the image can be optimized with the
     * selected scanline extractors or if the image must be rescaled to byte
     * before writing the image.
     */
    public boolean isScanlineSupported(RenderedImage image) {
        ScanlineProvider scanlines = ScanlineProviderFactory.getProvider(image);
        return scanlines != null;
    }

    private ImageInfo getImageInfo(RenderedImage image, ScanlineProvider scanlines,
            ColorModel colorModel, boolean indexed) {
        int numColorComponents = colorModel.getNumColorComponents();
        boolean grayscale = !indexed && numColorComponents < 3;
        byte bitDepth = scanlines.getBitDepth();
        boolean hasAlpha = !indexed && colorModel.hasAlpha();
        return new ImageInfo(image.getWidth(), image.getHeight(), bitDepth, hasAlpha, grayscale, indexed);
    }
}
