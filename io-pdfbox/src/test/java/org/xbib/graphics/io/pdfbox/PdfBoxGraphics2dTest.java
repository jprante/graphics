package org.xbib.graphics.io.pdfbox;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.junit.jupiter.api.Test;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.Iterator;

public class PdfBoxGraphics2dTest extends PdfBoxGraphics2DTestBase {

    @Test
    public void testNegativeShapesAndComposite() {
        exportGraphic("simple", "negativeWithComposite", gfx -> {
            RoundRectangle2D.Float rect = new RoundRectangle2D.Float(10f, 10f, 20f, 20f, 5f,
                    6f);

            AffineTransform transformIdentity = new AffineTransform();
            AffineTransform transformMirrored = AffineTransform.getTranslateInstance(0, 100);
            transformMirrored.scale(1, -0.5);
            for (AffineTransform tf : new AffineTransform[]{transformIdentity,
                    transformMirrored}) {
                gfx.setTransform(tf);
                gfx.setColor(Color.red);
                gfx.fill(rect);
                gfx.setStroke(new BasicStroke(2f));
                gfx.draw(rect);
                GradientPaint gp = new GradientPaint(10.0f, 25.0f, Color.blue, (float) 100,
                        (float) 100, Color.red);
                gfx.setPaint(gp);
                gfx.fill(AffineTransform.getTranslateInstance(30f, 20f)
                        .createTransformedShape(rect));
                Composite composite = gfx.getComposite();
                gfx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                gfx.setColor(Color.cyan);
                gfx.fillRect(15, 0, 40, 40);
                gfx.setColor(Color.green);
                gfx.drawRect(20, 10, 50, 50);
                gfx.setColor(Color.magenta);
                gfx.fill(new Ellipse2D.Double(20, 20, 100, 100));
                gfx.setColor(Color.orange);
                gfx.fill(new Ellipse2D.Double(20, 20, -100, 100));
                gfx.setPaint(gp);
                gfx.fill(new Ellipse2D.Double(10, 80, 20, 20));
                gfx.fill(new Ellipse2D.Double(10, 100, -20, -20));
                gfx.setComposite(composite);
            }

        });
    }

    @Test
    public void testGradients() {
        exportGraphic("simple", "gradients", gfx -> {
            LinearGradientPaint linearGradientPaint = new LinearGradientPaint(0, 0, 100, 200,
                    new float[]{0.0f, .2f, .4f, .9f, 1f},
                    new Color[]{Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE,
                            Color.GRAY});
            gfx.setPaint(linearGradientPaint);
            gfx.fill(new Rectangle.Float(10, 10, 100, 50));
            gfx.fill(new Rectangle.Float(120, 10, 50, 50));
            gfx.fill(new Rectangle.Float(200, 10, 50, 100));
            RadialGradientPaint radialGradientPaint = new RadialGradientPaint(200, 200, 200,
                    new float[]{0.0f, .2f, .4f, .9f, 1f},
                    new Color[]{Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE,
                            Color.GRAY});
            gfx.setPaint(radialGradientPaint);
            gfx.fill(new Rectangle.Float(10, 120, 100, 50));
            gfx.fill(new Rectangle.Float(120, 120, 50, 50));
            gfx.fill(new Rectangle.Float(200, 120, 50, 100));
        });
    }

    @Test
    public void testBuildPatternFill() {
        exportGraphic("simple", "patternfill", gfx -> {
            Composite composite = gfx.getComposite();
            RadialGradientPaint radialGradientPaint = new RadialGradientPaint(200, 200, 200,
                    new float[]{0.0f, .2f, .4f, .9f, 1f},
                    new Color[]{Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE,
                            Color.GRAY});
            gfx.setPaint(radialGradientPaint);
            gfx.setStroke(new BasicStroke(20));
            gfx.draw(new Ellipse2D.Double(100, 100, 80, 80));
            gfx.draw(new Ellipse2D.Double(150, 150, 50, 80));
            gfx.shear(0.4, 0.2);
            gfx.draw(new Ellipse2D.Double(150, 150, 50, 80));
            gfx.setComposite(composite);
        });
    }

    @Test
    public void testDifferentFonts() {
        exportGraphic("simple", "fonts", gfx -> {
            Font sansSerif = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
            Font embeddedFont = Font.createFont(Font.TRUETYPE_FONT,
                    PdfBoxGraphics2dTest.class.getResourceAsStream("DejaVuSerifCondensed.ttf"))
                    .deriveFont(15f);
            Font monoFont = Font.decode(Font.MONOSPACED).deriveFont(15f);
            Font serifFont = Font.decode(Font.SERIF).deriveFont(15f);
            int y = 50;
            for (Font f : new Font[]{sansSerif, embeddedFont, monoFont, serifFont}) {
                int x = 10;
                gfx.setPaint(Color.BLACK);
                gfx.setFont(f);
                String txt = f.getFontName() + ": ";
                gfx.drawString(txt, x, y);
                x += gfx.getFontMetrics().stringWidth(txt);

                txt = "Normal ";
                gfx.drawString(txt, x, y);
                x += gfx.getFontMetrics().stringWidth(txt);

                gfx.setPaint(new CMYKColor(1f, 0.5f, 1f, 0.1f, 128));
                txt = "Bold ";
                gfx.setFont(f.deriveFont(Font.BOLD));
                gfx.drawString(txt, x, y);
                x += gfx.getFontMetrics().stringWidth(txt);

                gfx.setPaint(new CMYKColor(128, 128, 128, 0));
                txt = "Italic ";
                gfx.setFont(f.deriveFont(Font.ITALIC));
                gfx.drawString(txt, x, y);
                x += gfx.getFontMetrics().stringWidth(txt);

                gfx.setPaint(new CMYKColor(255, 255, 255, 255));
                txt = "Bold-Italic ";
                gfx.setFont(f.deriveFont(Font.ITALIC | Font.BOLD));
                gfx.drawString(txt, x, y);
                gfx.getFontMetrics().stringWidth(txt);

                y += 30;
            }

        });
    }

    @Test
    public void testImageEncoding() {
        exportGraphic("imageenc", "imageenc", gfx -> {
            BufferedImage img2 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("pixeltest.png"));
            BufferedImage img3 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("Rose-ProPhoto.jpg"));
            BufferedImage img4 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("Italy-P3.jpg"));
            BufferedImage img5 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("16bit-image1.png"));
            BufferedImage img6 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("16bit-image2.png"));

            gfx.drawImage(img2, 70, 50, 100, 50, null);
            gfx.drawImage(img3, 30, 200, 75, 50, null);
            gfx.drawImage(img4, 170, 10, 60, 40, null);
            gfx.drawImage(img5, 270, 10, 16, 16, null);
            gfx.drawImage(img5, 270, 30, 64, 64, null);
            gfx.drawImage(img6, 270, 200, 100, 100, null);
        });
    }

    @Test
    public void testEvenOddRules() {

        exportGraphic("simple", "evenOdd", gfx -> {
            gfx.setColor(Color.YELLOW);
            gfx.fillPolygon(new int[]{80, 129, 0, 160, 31},
                    new int[]{0, 152, 58, 58, 152}, 5);
            Path2D.Double s = new Path2D.Double();
            s.moveTo(80, 0);
            s.lineTo(129, 152);
            s.lineTo(0, 58);
            s.lineTo(160, 58);
            s.lineTo(31, 152);
            s.setWindingRule(Path2D.WIND_EVEN_ODD);
            gfx.setColor(Color.BLUE);
            gfx.translate(200, 0);
            gfx.fill(s);
            s.setWindingRule(Path2D.WIND_NON_ZERO);
            gfx.setColor(Color.GREEN);
            gfx.translate(0, 200);
            gfx.fill(s);
        });
    }

    @Test
    public void testSimpleGraphics2d() {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("JPEG");
        while (readers.hasNext()) {
            readers.next();
        }
        exportGraphic("simple", "simple", gfx -> {
            BufferedImage imgColorTest = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("colortest.png"));
            BufferedImage img2 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("pixeltest.png"));
            BufferedImage img3 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("Rose-ProPhoto.jpg"));
            BufferedImage img4 = ImageIO
                    .read(PdfBoxGraphics2dTest.class.getResourceAsStream("Italy-P3.jpg"));

            gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            gfx.drawImage(imgColorTest, 70, 50, 100, 50, null);

            gfx.drawImage(img3, 30, 200, 75, 50, null);
            gfx.drawImage(img3, 110, 200, 50, 50, null);
            gfx.drawImage(img4, 170, 10, 60, 40, null);

            gfx.setColor(Color.YELLOW);
            gfx.drawRect(20, 20, 100, 100);
            gfx.setColor(Color.GREEN);
            gfx.fillRect(10, 10, 50, 50);

            gfx.setColor(new CMYKColor(255, 128, 0, 128, 200));
            gfx.drawString("Hello World!", 30, 120);
            gfx.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            gfx.drawImage(img2, 30, 50, 50, 50, null);

            Font font = new Font("SansSerif", Font.PLAIN, 30);
            Font font2 = Font.createFont(Font.TRUETYPE_FONT,
                    PdfBoxGraphics2dTest.class.getResourceAsStream("DejaVuSerifCondensed.ttf"))
                    .deriveFont(20f);
            final String words = "Valour fate kinship darkness";

            AttributedString as1 = new AttributedString(words);
            as1.addAttribute(TextAttribute.FONT, font);

            Rectangle2D valour = font2.getStringBounds("Valour", gfx.getFontRenderContext());
            GradientPaint gp = new GradientPaint(10.0f, 25.0f, Color.blue,
                    (float) valour.getWidth(), (float) valour.getHeight(), Color.red);

            gfx.setColor(Color.GREEN);
            as1.addAttribute(TextAttribute.FOREGROUND, gp, 0, 6);
            as1.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON, 0, 6);
            as1.addAttribute(TextAttribute.FONT, font2, 0, 6);
            as1.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON, 7, 11);
            as1.addAttribute(TextAttribute.BACKGROUND, Color.LIGHT_GRAY, 12, 19);
            as1.addAttribute(TextAttribute.FONT, font2, 20, 28);
            as1.addAttribute(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON, 20, 28);
            as1.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON, 20, 28);
            gfx.drawString(as1.getIterator(), 15, 160);

            // Hello World - in arabic and hebrew
            Font font3 = new Font("SansSerif", Font.PLAIN, 40);
            gfx.setFont(font3);
            gfx.setColor(Color.BLACK);
            gfx.drawString("مرحبا بالعالم", 200, 100);
            gfx.setPaint(
                    new TexturePaint(imgColorTest, new Rectangle2D.Float(5f, 7f, 100f, 20f)));
            gfx.drawString("مرحبا بالعالم", 200, 250);
            gfx.drawString("שלום עולם", 200, 200);

            gfx.setClip(new Ellipse2D.Float(360, 360, 60, 80));
            gfx.fillRect(300, 300, 100, 100);
            gfx.setClip(null);
            gfx.fillRect(360, 360, 10, 10);

        });
    }
}
