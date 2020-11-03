package org.xbib.graphics.chart.io.vector.util;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

public class ImageDataStream extends InputStream {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final Interleaving interleaving;
    private final Raster raster;
    private final boolean opaque;
    private final Queue<Integer> byteBuffer;
    private final int[] sampleValues;
    private final int[] sampleSizes;
    private int x;
    private int y;

    public ImageDataStream(BufferedImage image, Interleaving interleaving) {
        this.image = image;
        this.interleaving = interleaving;

        width = image.getWidth();
        height = image.getHeight();
        x = -1;
        y = 0;

        Raster alphaRaster = image.getAlphaRaster();
        if (interleaving == Interleaving.ALPHA_ONLY) {
            raster = alphaRaster;
        } else {
            raster = image.getRaster();
        }
        opaque = alphaRaster == null;

        byteBuffer = new LinkedList<Integer>();
        sampleValues = new int[raster.getNumBands()];
        sampleSizes = raster.getSampleModel().getSampleSize();
    }

    public BufferedImage getImage() {
        return image;
    }

    public Interleaving getInterleaving() {
        return interleaving;
    }

    @Override
    public int read() throws IOException {
        if (!byteBuffer.isEmpty()) {
            return byteBuffer.poll();
        } else {
            if (!nextSample()) {
                return -1;
            }
            int bands = sampleValues.length;
            if (interleaving == Interleaving.WITHOUT_ALPHA ||
                    interleaving == Interleaving.ALPHA_ONLY) {
                if (interleaving == Interleaving.WITHOUT_ALPHA && !opaque) {
                    // Ignore alpha band
                    bands--;
                }
                for (int band = 0; band < bands; band++) {
                    bufferSampleValue(band);
                }
            } else {
                if (opaque) {
                    for (int band = 0; band < bands; band++) {
                        bufferSampleValue(band);
                    }
                } else {
                    for (int band = 0; band < bands; band++) {
                        // Fix order to be ARGB instead of RGBA
                        if (band == 0) {
                            bufferSampleValue(bands - 1);
                        } else {
                            bufferSampleValue(band - 1);
                        }
                    }
                }
            }
            if (!byteBuffer.isEmpty()) {
                return byteBuffer.poll();
            } else {
                return -1;
            }
        }
    }

    private void bufferSampleValue(int band) {
        if (sampleSizes[band] < 8) {
            int byteValue = sampleValues[band] & 0xFF;
            byteBuffer.offer(byteValue);
        } else {
            int byteCount = sampleSizes[band] / 8;
            for (int i = byteCount - 1; i >= 0; i--) {
                int byteValue = (sampleValues[band] >> i * 8) & 0xFF;
                byteBuffer.offer(byteValue);
            }
        }
    }

    private boolean nextSample() {
        if (interleaving == Interleaving.SAMPLE || interleaving == Interleaving.WITHOUT_ALPHA) {
            x++;
            if (x >= width) {
                x = 0;
                y++;
            }
        }
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        } else {
            raster.getPixel(x, y, sampleValues);
            return true;
        }
    }

    public enum Interleaving {
        SAMPLE,
        ROW,
        WITHOUT_ALPHA,
        ALPHA_ONLY
    }
}

