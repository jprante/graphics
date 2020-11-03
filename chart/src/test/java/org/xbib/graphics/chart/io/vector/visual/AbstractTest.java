package org.xbib.graphics.chart.io.vector.visual;

import org.xbib.graphics.chart.io.vector.EPSGraphics2D;
import org.xbib.graphics.chart.io.vector.PDFGraphics2D;
import org.xbib.graphics.chart.io.vector.SVGGraphics2D;
import org.xbib.graphics.chart.io.vector.util.PageSize;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractTest {

    private final PageSize pageSize;

    private final BufferedImage reference;

    private final EPSGraphics2D epsGraphics;

    private final PDFGraphics2D pdfGraphics;

    private final SVGGraphics2D svgGraphics;

    public AbstractTest() {
        int width = 150;
        int height = 150;
        pageSize = new PageSize(0.0, 0.0, width, height);
        epsGraphics = new EPSGraphics2D(0, 0, width, height);
        draw(epsGraphics);
        pdfGraphics = new PDFGraphics2D(0, 0, width, height);
        draw(pdfGraphics);
        svgGraphics = new SVGGraphics2D(0, 0, width, height);
        draw(svgGraphics);
        reference = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D referenceGraphics = reference.createGraphics();
        referenceGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        referenceGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        referenceGraphics.setBackground(new Color(1f, 1f, 1f, 0f));
        referenceGraphics.clearRect(0, 0, reference.getWidth(), reference.getHeight());
        referenceGraphics.setColor(Color.BLACK);
        draw(referenceGraphics);
        try {
            File referenceImage = File.createTempFile(getClass().getName() + ".reference", ".png");
            referenceImage.deleteOnExit();
            ImageIO.write(reference, "png", referenceImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract void draw(Graphics2D g);

    public PageSize getPageSize() {
        return pageSize;
    }

    public BufferedImage getReference() {
        return reference;
    }

    public InputStream getEPS() {
        return new ByteArrayInputStream(epsGraphics.getBytes());
    }

    public InputStream getPDF() {
        return new ByteArrayInputStream(pdfGraphics.getBytes());
    }

    public InputStream getSVG() {
        return new ByteArrayInputStream(svgGraphics.getBytes());
    }

    /*public BufferedImage getRasterizedEPS() throws GhostscriptException, IOException {
        if (rasterizedEPS != null) {
            return rasterizedEPS;
        }

        File epsInputFile = File.createTempFile(getClass().getName() + ".testEPS", ".eps");
        epsInputFile.deleteOnExit();
        OutputStream epsInput = new FileOutputStream(epsInputFile);
        epsInput.write(epsGraphics.getBytes());
        epsInput.close();

        File pngOutputFile = File.createTempFile(getClass().getName() + ".testEPS", "png");
        pngOutputFile.deleteOnExit();
        Ghostscript gs = Ghostscript.getInstance();
        gs.initialize(new String[]{
                "-dBATCH",
                "-dQUIET",
                "-dNOPAUSE",
                "-dSAFER",
                String.format("-g%dx%d", Math.round(getPageSize().width), Math.round(getPageSize().height)),
                "-dGraphicsAlphaBits=4",
                "-dAlignToPixels=0",
                "-dEPSCrop",
                "-dPSFitPage",
                "-sDEVICE=pngalpha",
                "-sOutputFile=" + pngOutputFile.toString(),
                epsInputFile.toString()
        });
        gs.exit();
        rasterizedEPS = ImageIO.read(pngOutputFile);
        return rasterizedEPS;
    }*/

    /*public BufferedImage getRasterizedPDF() throws GhostscriptException, IOException {
        if (rasterizedPDF != null) {
            return rasterizedPDF;
        }

        File pdfInputFile = File.createTempFile(getClass().getName() + ".testPDF", ".pdf");
        pdfInputFile.deleteOnExit();
        OutputStream pdfInput = new FileOutputStream(pdfInputFile);
        pdfInput.write(pdfGraphics.getBytes());
        pdfInput.close();

        File pngOutputFile = File.createTempFile(getClass().getName() + ".testPDF", "png");
        pngOutputFile.deleteOnExit();
        Ghostscript gs = Ghostscript.getInstance();
        gs.initialize(new String[]{
                "-dBATCH",
                "-dQUIET",
                "-dNOPAUSE",
                "-dSAFER",
                String.format("-g%dx%d", Math.round(getPageSize().width), Math.round(getPageSize().height)),
                "-dGraphicsAlphaBits=4",
                // TODO: More robust settings for gs? DPI value is estimated.
                "-r25",
                "-dAlignToPixels=0",
                "-dPDFFitPage",
                "-sDEVICE=pngalpha",
                "-sOutputFile=" + pngOutputFile.toString(),
                pdfInputFile.toString()
        });
        gs.exit();
        rasterizedPDF = ImageIO.read(pngOutputFile);
        return rasterizedPDF;
    }*/

    /*public BufferedImage getRasterizedSVG() throws TranscoderException {
        if (rasterizedSVG != null) {
            return rasterizedSVG;
        }

        rasterizedSVG = new BufferedImage(
                (int) Math.round(getPageSize().width), (int) Math.round(getPageSize().height),
                BufferedImage.TYPE_INT_ARGB);

        ImageTranscoder transcoder = new ImageTranscoder() {
            @Override
            public BufferedImage createImage(int width, int height) {
                return rasterizedSVG;
            }

            @Override
            public void writeImage(BufferedImage bufferedImage, TranscoderOutput transcoderOutput) throws TranscoderException {
            }
        };

        transcoder.transcode(new TranscoderInput(getSVG()), null);

        return rasterizedSVG;
    }*/
}
