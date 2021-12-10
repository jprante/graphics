package org.xbib.graphics.io.visual;

import org.xbib.graphics.io.vector.PageSize;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public abstract class AbstractTest {

    private final PageSize pageSize;

    private final BufferedImage reference;

    //private final EPSGraphics2D epsGraphics;

    //private final PDFGraphics2D pdfGraphics;

    //private final SVGGraphics2D svgGraphics;

    public AbstractTest() throws IOException {
        int width = 150;
        int height = 150;
        pageSize = new PageSize(0.0, 0.0, width, height);
        //epsGraphics = new EPSGraphics2D(0, 0, width, height);
        //draw(epsGraphics);
        //pdfGraphics = new PDFGraphics2D(0, 0, width, height);
        //draw(pdfGraphics);
        //svgGraphics = new SVGGraphics2D(0, 0, width, height);
        //draw(svgGraphics);
        reference = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D referenceGraphics = reference.createGraphics();
        referenceGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        referenceGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        referenceGraphics.setBackground(new Color(1f, 1f, 1f, 0f));
        referenceGraphics.clearRect(0, 0, reference.getWidth(), reference.getHeight());
        referenceGraphics.setColor(Color.BLACK);
        draw(referenceGraphics);
        Path path = Files.createTempFile(Paths.get("build"), getClass().getName() + ".reference", "png");
        ImageIO.write(reference, "png", path.toFile());
    }

    public abstract void draw(Graphics2D g);

    public PageSize getPageSize() {
        return pageSize;
    }

    public BufferedImage getReference() {
        return reference;
    }

    /*public InputStream getEPS() {
        try {
            return new ByteArrayInputStream(epsGraphics.getBytes());
        } catch (IOException e) {
            return null;
        }
    }

    public InputStream getPDF() {
        try {
            return new ByteArrayInputStream(pdfGraphics.getBytes());
        } catch (IOException e) {
            return null;
        }
    }

    public InputStream getSVG() {
        try {
            return new ByteArrayInputStream(svgGraphics.getBytes());
        } catch (IOException e) {
            return null;
        }
    }*/
}
