package org.xbib.graphics.io.vector.eps;

import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.GraphicsState;
import org.xbib.graphics.io.vector.PageSize;
import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import org.xbib.graphics.io.vector.commands.DrawImageCommand;
import org.xbib.graphics.io.vector.commands.DrawShapeCommand;
import org.xbib.graphics.io.vector.commands.DrawStringCommand;
import org.xbib.graphics.io.vector.commands.FillShapeCommand;
import org.xbib.graphics.io.vector.commands.RotateCommand;
import org.xbib.graphics.io.vector.commands.ScaleCommand;
import org.xbib.graphics.io.vector.commands.SetClipCommand;
import org.xbib.graphics.io.vector.commands.SetColorCommand;
import org.xbib.graphics.io.vector.commands.SetCompositeCommand;
import org.xbib.graphics.io.vector.commands.SetFontCommand;
import org.xbib.graphics.io.vector.commands.SetPaintCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.commands.ShearCommand;
import org.xbib.graphics.io.vector.commands.TransformCommand;
import org.xbib.graphics.io.vector.commands.TranslateCommand;
import org.xbib.graphics.io.vector.eps.util.ASCII85EncodeStream;
import org.xbib.graphics.io.vector.eps.util.AlphaToMaskOp;
import org.xbib.graphics.io.vector.eps.util.FlateEncodeStream;
import org.xbib.graphics.io.vector.eps.util.ImageDataStream;
import org.xbib.graphics.io.vector.eps.util.LineWrapOutputStream;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;

public class EPSProcessorResult implements ProcessorResult {
    /**
     * Constant to convert values from millimeters to PostScript® units
     * (1/72th inch).
     */
    private static final double UNITS_PER_MM = 72.0 / 25.4;

    private static final String CHARSET = "ISO-8859-1";

    private static final String EOL = "\n";

    private static final int MAX_LINE_WIDTH = 255;

    private static final Pattern ELEMENT_SEPARATION_PATTERN = Pattern.compile("(.{1," + MAX_LINE_WIDTH + "})(\\s+|$)");

    /**
     * Mapping of stroke endcap values from Java to PostScript®.
     */
    private static final Map<Integer, Integer> STROKE_ENDCAPS = map(
            new Integer[]{BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE},
            new Integer[]{0, 1, 2}
    );

    /**
     * Mapping of line join values for path drawing from Java to
     * PostScript®.
     */
    private static final Map<Integer, Integer> STROKE_LINEJOIN = map(
            new Integer[]{BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL},
            new Integer[]{0, 1, 2}
    );

    private static final String FONT_LATIN1_SUFFIX = "Lat";

    private final PageSize pageSize;

    private final List<String> elements;

    public EPSProcessorResult(PageSize pageSize) {
        this.pageSize = pageSize;
        this.elements = new LinkedList<>();
        addHeader();
    }

    private static String getOutput(Color c) {
        return c.getRed() / 255.0 + " " + c.getGreen() / 255.0 + " " + c.getBlue() / 255.0 + " rgb";
    }

    private static String getOutput(Shape s) {
        StringBuilder out = new StringBuilder();
        out.append("newpath ");
        if (s instanceof Line2D) {
            Line2D l = (Line2D) s;
            out.append(l.getX1()).append(" ").append(l.getY1()).append(" M ")
                    .append(l.getX2()).append(" ").append(l.getY2()).append(" L");
        } else if (s instanceof Rectangle2D) {
            Rectangle2D r = (Rectangle2D) s;
            out.append(r.getX()).append(" ").append(r.getY()).append(" ")
                    .append(r.getWidth()).append(" ").append(r.getHeight())
                    .append(" rect Z");
        } else if (s instanceof Ellipse2D) {
            Ellipse2D e = (Ellipse2D) s;
            double x = e.getX() + e.getWidth() / 2.0;
            double y = e.getY() + e.getHeight() / 2.0;
            double rx = e.getWidth() / 2.0;
            double ry = e.getHeight() / 2.0;
            out.append(x).append(" ").append(y).append(" ")
                    .append(rx).append(" ").append(ry).append(" ")
                    .append(360.0).append(" ").append(0.0)
                    .append(" ellipse Z");
        } else if (s instanceof Arc2D) {
            Arc2D e = (Arc2D) s;
            double x = (e.getX() + e.getWidth() / 2.0);
            double y = (e.getY() + e.getHeight() / 2.0);
            double rx = e.getWidth() / 2.0;
            double ry = e.getHeight() / 2.0;
            double startAngle = -e.getAngleStart();
            double endAngle = -(e.getAngleStart() + e.getAngleExtent());
            out.append(x).append(" ").append(y).append(" ")
                    .append(rx).append(" ").append(ry).append(" ")
                    .append(startAngle).append(" ").append(endAngle)
                    .append(" ellipse");
            if (e.getArcType() == Arc2D.CHORD) {
                out.append(" Z");
            } else if (e.getArcType() == Arc2D.PIE) {
                out.append(" ").append(x).append(" ").append(y).append(" L Z");
            }
        } else {
            PathIterator segments = s.getPathIterator(null);
            double[] coordsCur = new double[6];
            double[] pointPrev = new double[2];
            for (int i = 0; !segments.isDone(); i++, segments.next()) {
                if (i > 0) {
                    out.append(" ");
                }
                int segmentType = segments.currentSegment(coordsCur);
                switch (segmentType) {
                    case PathIterator.SEG_MOVETO:
                        out.append(coordsCur[0]).append(" ").append(coordsCur[1])
                                .append(" M");
                        pointPrev[0] = coordsCur[0];
                        pointPrev[1] = coordsCur[1];
                        break;
                    case PathIterator.SEG_LINETO:
                        out.append(coordsCur[0]).append(" ").append(coordsCur[1])
                                .append(" L");
                        pointPrev[0] = coordsCur[0];
                        pointPrev[1] = coordsCur[1];
                        break;
                    case PathIterator.SEG_CUBICTO:
                        out.append(coordsCur[0]).append(" ").append(coordsCur[1])
                                .append(" ").append(coordsCur[2]).append(" ")
                                .append(coordsCur[3]).append(" ").append(coordsCur[4])
                                .append(" ").append(coordsCur[5]).append(" C");
                        pointPrev[0] = coordsCur[4];
                        pointPrev[1] = coordsCur[5];
                        break;
                    case PathIterator.SEG_QUADTO:
                        double x1 = pointPrev[0] + 2.0 / 3.0 * (coordsCur[0] - pointPrev[0]);
                        double y1 = pointPrev[1] + 2.0 / 3.0 * (coordsCur[1] - pointPrev[1]);
                        double x2 = coordsCur[0] + 1.0 / 3.0 * (coordsCur[2] - coordsCur[0]);
                        double y2 = coordsCur[1] + 1.0 / 3.0 * (coordsCur[3] - coordsCur[1]);
                        double x3 = coordsCur[2];
                        double y3 = coordsCur[3];
                        out.append(x1).append(" ").append(y1).append(" ")
                                .append(x2).append(" ").append(y2).append(" ")
                                .append(x3).append(" ").append(y3).append(" C");
                        pointPrev[0] = x3;
                        pointPrev[1] = y3;
                        break;
                    case PathIterator.SEG_CLOSE:
                        out.append("Z");
                        break;
                    default:
                        throw new IllegalStateException("Unknown path operation.");
                }
            }
        }
        return out.toString();
    }

    private static String getOutput(Image image, int imageWidth, int imageHeight,
                                    double x, double y, double width, double height) throws IOException {
        StringBuilder out = new StringBuilder();
        BufferedImage bufferedImage = toBufferedImage(image);
        int bands = bufferedImage.getSampleModel().getNumBands();
        int bitsPerSample = max(bufferedImage.getSampleModel().getSampleSize());
        bitsPerSample = (int) (Math.ceil(bitsPerSample / 8.0) * 8.0);
        if (bands > 3) {
            bands = 3;
        }
        out.append("gsave").append(EOL);
        if (x != 0.0 || y != 0.0) {
            out.append(x).append(" ").append(y).append(" translate").append(EOL);
        }
        if (width != 1.0 || height != 1.0) {
            out.append(width).append(" ").append(height).append(" scale").append(EOL);
        }
        int decodeScale = 1;
        if (bufferedImage.getColorModel().hasAlpha()) {
            out.append("<< /ImageType 3 /InterleaveType 1 ")
                    .append("/MaskDict ")
                    .append(imageWidth).append(" ").append(imageHeight).append(" ")
                    .append(1).append(" ").append(bitsPerSample).append(" ").append(decodeScale).append(" ")
                    .append(false).append(" ").append(0).append(" imgdict ")
                    .append("/DataDict ")
                    .append(imageWidth).append(" ").append(imageHeight).append(" ")
                    .append(bands).append(" ").append(bitsPerSample).append(" ").append(decodeScale).append(" ")
                    .append(true).append(" currentfile /ASCII85Decode filter ")
                    .append("<< /BitsPerComponent ").append(bitsPerSample).append(" >> ")
                    .append("/FlateDecode filter ")
                    .append("imgdict ")
                    .append(">> image").append(EOL);
            bufferedImage = new AlphaToMaskOp(true).filter(bufferedImage, null);
            output(bufferedImage, out);
        } else {
            if (bands == 1) {
                out.append("/DeviceGray setcolorspace").append(EOL);
            }
            if (bufferedImage.getType() == BufferedImage.TYPE_BYTE_BINARY) {
                decodeScale = 255;
            }
            out.append(imageWidth).append(" ").append(imageHeight).append(" ")
                    .append(bands).append(" ").append(bitsPerSample).append(" ").append(decodeScale).append(" ")
                    .append(true).append(" currentfile /ASCII85Decode filter ")
                    .append("<< /BitsPerComponent ").append(bitsPerSample).append(" >> ")
                    .append("/FlateDecode filter ")
                    .append("imgdict ")
                    .append("image").append(EOL);
            output(bufferedImage, out);
        }
        out.append("grestore");
        return out.toString();
    }

    private static void output(BufferedImage image, StringBuilder out) throws IOException {
        InputStream imageDataStream = new ImageDataStream(image, ImageDataStream.Interleaving.SAMPLE);
        ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
        OutputStream compressionStream =
                new FlateEncodeStream(new ASCII85EncodeStream(new LineWrapOutputStream(outBytes, 80)));
        imageDataStream.transferTo(compressionStream);
        compressionStream.close();
        String compressed = outBytes.toString(CHARSET);
        out.append(compressed).append(EOL);
    }

    private static String getOutput(String str, double x, double y) {
        return "gsave 1 -1 scale " + x + " " + -y + " M " + getOutput(str) + " show " + "grestore";
    }

    private static StringBuilder getOutput(String str) {
        StringBuilder out = new StringBuilder();
        // Escape text
        str = str.replaceAll("\\\\", "\\\\\\\\")
                .replaceAll("\t", "\\\\t")
                .replaceAll("\b", "\\\\b")
                .replaceAll("\f", "\\\\f")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)")
                .replaceAll("[\r\n]", "");
        out.append("(").append(str).append(")");
        return out;
    }

    private static String getOutput(Stroke s) {
        StringBuilder out = new StringBuilder();
        if (s instanceof BasicStroke) {
            BasicStroke bs = (BasicStroke) s;
            float[] f = bs.getDashArray();
            String dash = f == null ? "" : IntStream.range(0, f.length)
                    .mapToDouble(i -> f[i])
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining(" "));
            out.append(bs.getLineWidth()).append(" setlinewidth ")
                    .append(STROKE_LINEJOIN.get(bs.getLineJoin())).append(" setlinejoin ")
                    .append(STROKE_ENDCAPS.get(bs.getEndCap())).append(" setlinecap ")
                    .append("[").append(dash).append("] ")
                    .append(bs.getDashPhase()).append(" setdash");
        } else {
            out.append("% Custom strokes aren't supported at the moment");
        }
        return out.toString();
    }

    private static String getOutput(Font font) {
        StringBuilder out = new StringBuilder();
        font = getPhysicalFont(font);
        String fontName = font.getPSName();
        String fontNameLatin1 = fontName + FONT_LATIN1_SUFFIX;
        out.append("/").append(fontNameLatin1).append(" ")
                .append("/").append(font.getPSName()).append(" latinize ");
        out.append("/").append(fontNameLatin1).append(" ")
                .append(font.getSize2D()).append(" selectfont");
        return out.toString();
    }

    private void addHeader() {
        double x = pageSize.getX() * UNITS_PER_MM,
                y = pageSize.getY() * UNITS_PER_MM,
                width = pageSize.getWidth() * UNITS_PER_MM,
                height = pageSize.getHeight() * UNITS_PER_MM;
        elements.addAll(Arrays.asList(
                "%!PS-Adobe-3.0 EPSF-3.0",
                "%%BoundingBox: " + ((int) Math.floor(x)) + " " + ((int) Math.floor(y)) + " " + ((int) Math.ceil(x + width)) + " " + ((int) Math.ceil(y + height)),
                "%%HiResBoundingBox: " + x + " " + y + " " + (x + width) + " " + (y + height),
                "%%LanguageLevel: 3",
                "%%Pages: 1",
                "%%EndComments",
                "%%Page: 1 1",
                "/M /moveto load def",
                "/L /lineto load def",
                "/C /curveto load def",
                "/Z /closepath load def",
                "/RL /rlineto load def",
                "/rgb /setrgbcolor load def",
                "/rect { /height exch def /width exch def /y exch def /x exch def x y M width 0 RL 0 height RL width neg 0 RL } bind def",
                "/ellipse { /endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def /savematrix matrix currentmatrix def x y translate rx ry scale 0 0 1 startangle endangle arcn savematrix setmatrix } bind def",
                "/imgdict { /datastream exch def /hasdata exch def /decodeScale exch def /bits exch def /bands exch def /imgheight exch def /imgwidth exch def << /ImageType 1 /Width imgwidth /Height imgheight /BitsPerComponent bits /Decode [bands {0 decodeScale} repeat] /ImageMatrix [imgwidth 0 0 imgheight 0 0] hasdata { /DataSource datastream } if >> } bind def",
                "/latinize { /fontName exch def /fontNameNew exch def fontName findfont 0 dict copy begin /Encoding ISOLatin1Encoding def fontNameNew /FontName def currentdict end dup /FID undef fontNameNew exch definefont pop } bind def",
                getOutput(GraphicsState.DEFAULT_FONT),
                "gsave",
                "clipsave",
                "/DeviceRGB setcolorspace",
                "0 " + height + " translate",
                UNITS_PER_MM + " " + (-UNITS_PER_MM) + " scale",
                "/basematrix matrix currentmatrix def"
        ));
    }

    public void write(OutputStream out) throws IOException {
        OutputStreamWriter o = new OutputStreamWriter(out, CHARSET);
        for (String element : elements) {
            if (element == null) {
                continue;
            }
            // Write current element in lines of 255 bytes (excluding line terminators)
            // Numbers must not be separated by line breaks or errors will occur
            // TODO: Integrate functionality into LineWrapOutputStream
            Matcher chunkMatcher = ELEMENT_SEPARATION_PATTERN.matcher(element);
            boolean chunkFound = false;
            while (chunkMatcher.find()) {
                chunkFound = true;
                String chunk = chunkMatcher.group();
                o.write(chunk, 0, chunk.length());
                o.append(EOL);
            }
            if (!chunkFound) {
                // TODO: Exception, if no whitespace can be found in the chunk
                throw new IllegalStateException("Unable to divide eps element into lines: " + element);
            }
        }
        o.append("%%EOF");
        o.flush();
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public void handle(Command<?> command) throws IOException {
        if (command instanceof SetClipCommand) {
            SetClipCommand c = (SetClipCommand) command;
            Shape clip = c.getValue();
            elements.add("cliprestore");
            if (clip != null) {
                elements.add(getOutput(clip) + " clip");
            }
        } else if (command instanceof SetColorCommand) {
            SetColorCommand c = (SetColorCommand) command;
            elements.add(getOutput(c.getValue()));
        } else if (command instanceof SetCompositeCommand) {
            SetCompositeCommand c = (SetCompositeCommand) command;
            elements.add("% composite not yet implemented: " + c.getValue());
        } else if (command instanceof SetFontCommand) {
            SetFontCommand c = (SetFontCommand) command;
            elements.add(getOutput(c.getValue()));
        } else if (command instanceof SetPaintCommand) {
            SetPaintCommand c = (SetPaintCommand) command;
            elements.add("% paint not yet implemented: " + c.getValue());
        } else if (command instanceof SetStrokeCommand) {
            SetStrokeCommand c = (SetStrokeCommand) command;
            elements.add(getOutput(c.getValue()));
        } else if (command instanceof SetTransformCommand) {
            SetTransformCommand c = (SetTransformCommand) command;
            StringBuilder e = new StringBuilder();
            double[] matrix = new double[6];
            c.getValue().getMatrix(matrix);
            String j = Arrays.stream(matrix).mapToObj(String::valueOf).collect(Collectors.joining(" "));
            e.append("basematrix setmatrix [").append(j).append("] concat");
            elements.add(e.toString());
        } else if (command instanceof RotateCommand) {
            RotateCommand c = (RotateCommand) command;
            StringBuilder e = new StringBuilder();
            double x = c.getCenterX();
            double y = c.getCenterY();
            boolean translated = x != 0.0 || y != 0.0;
            if (translated) {
                e.append(x).append(" ").append(y).append(" translate ");
            }
            e.append(Math.toDegrees(c.getTheta())).append(" rotate");
            if (translated) {
                e.append(" ");
                e.append(-x).append(" ").append(-y).append(" translate");
            }
            elements.add(e.toString());
        } else if (command instanceof ScaleCommand) {
            ScaleCommand c = (ScaleCommand) command;
            elements.add(format(c.getScaleX()) + " " + format(c.getScaleY()) + " scale");
        } else if (command instanceof ShearCommand) {
            ShearCommand c = (ShearCommand) command;
            elements.add("[1 " + format(c.getShearY()) + " " + format(c.getShearX()) + " 1 0 0] concat");
        } else if (command instanceof TransformCommand) {
            TransformCommand c = (TransformCommand) command;
            StringBuilder e = new StringBuilder();
            double[] matrix = new double[6];
            c.getValue().getMatrix(matrix);
            String j = Arrays.stream(matrix).mapToObj(String::valueOf).collect(Collectors.joining(" "));
            e.append("[").append(j).append("] concat");
            elements.add(e.toString());
        } else if (command instanceof TranslateCommand) {
            TranslateCommand c = (TranslateCommand) command;
            elements.add(c.getDeltaX() + " " + c.getDeltaY() + " translate");
        } else if (command instanceof DrawImageCommand) {
            DrawImageCommand c = (DrawImageCommand) command;
            String e = getOutput(c.getValue(),
                    c.getImageWidth(), c.getImageHeight(),
                    c.getX(), c.getY(), c.getWidth(), c.getHeight());
            elements.add(e);
        } else if (command instanceof DrawShapeCommand) {
            DrawShapeCommand c = (DrawShapeCommand) command;
            elements.add(getOutput(c.getValue()) + " stroke");
        } else if (command instanceof DrawStringCommand) {
            DrawStringCommand c = (DrawStringCommand) command;
            elements.add(getOutput(c.getValue(), c.getX(), c.getY()));
        } else if (command instanceof FillShapeCommand) {
            FillShapeCommand c = (FillShapeCommand) command;
            elements.add(getOutput(c.getValue()) + " fill");
        } else if (command instanceof CreateCommand) {
            elements.add("gsave");
        } else if (command instanceof DisposeCommand) {
            elements.add("grestore");
        }
    }

    /**
     * Returns a formatted string of the specified number. All trailing zeroes
     * or decimal points will be stripped.
     *
     * @param number Number to convert to a string.
     * @return A formatted string.
     */
    private static String format(Number number) {
        String formatted;
        if (number instanceof Double || number instanceof Float) {
            formatted = Double.toString(number.doubleValue())
                    .replaceAll("\\.0+$", "")
                    .replaceAll("(\\.[0-9]*[1-9])0+$", "$1");
        } else {
            formatted = number.toString();
        }
        return formatted;
    }

    /**
     * Creates a mapping from two arrays, one with keys, one with values.
     *
     * @param <K>    Data type of the keys.
     * @param <V>    Data type of the values.
     * @param keys   Array containing the keys.
     * @param values Array containing the values.
     * @return Map with keys and values from the specified arrays.
     */
    private static <K, V> Map<K, V> map(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new IllegalArgumentException("Cannot create a Map: number of keys and values differs.");
        }
        Map<K, V> map = new LinkedHashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * Returns the largest of all specified values.
     *
     * @param values Several integer values.
     * @return largest value.
     */
    private static int max(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * This method returns a buffered image with the contents of an image.
     * Taken from http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     *
     * @param image Image to be converted
     * @return a buffered image with the contents of the specified image
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        image = new ImageIcon(image).getImage();
        boolean hasAlpha = hasAlpha(image);
        BufferedImage bimage;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.TRANSLUCENT;
            }
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            bimage = null;
        }
        if (bimage == null) {
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }
        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }

    /**
     * This method returns {@code true} if the specified image has the
     * possibility to store transparent pixels.
     * Inspired by http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
     *
     * @param image Image that should be checked for alpha channel.
     * @return {@code true} if the specified image can have transparent pixels,
     * {@code false} otherwise
     */
    private static boolean hasAlpha(Image image) {
        ColorModel cm;
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            cm = bimage.getColorModel();
        } else {
            PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
            try {
                pg.grabPixels();
            } catch (InterruptedException e) {
                return false;
            }
            cm = pg.getColorModel();
        }
        return cm.hasAlpha();
    }

    /**
     * Try to guess physical font from the properties of a logical font, like
     * "Dialog", "Serif", "Monospaced" etc.
     *
     * @param logicalFont Logical font object.
     * @param testText    Text used to determine font properties.
     * @return An object of the first matching physical font. The original font
     * object is returned if it was a physical font or no font matched.
     */
    private static Font getPhysicalFont(Font logicalFont, String testText) {
        String logicalFamily = logicalFont.getFamily();
        if (!isLogicalFontFamily(logicalFamily)) {
            return logicalFont;
        }
        final TextLayout logicalLayout = new TextLayout(testText, logicalFont, FONT_RENDER_CONTEXT);
        Queue<Font> physicalFonts = new PriorityQueue<>(1, FONT_EXPRESSIVENESS_COMPARATOR);
        Font[] allPhysicalFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font physicalFont : allPhysicalFonts) {
            String physicalFamily = physicalFont.getFamily();
            if (isLogicalFontFamily(physicalFamily)) {
                continue;
            }
            physicalFont = physicalFont.deriveFont(logicalFont.getStyle(), logicalFont.getSize2D());
            TextLayout physicalLayout = new TextLayout(testText, physicalFont, FONT_RENDER_CONTEXT);
            if (physicalLayout.getBounds().equals(logicalLayout.getBounds()) &&
                    physicalLayout.getAscent() == logicalLayout.getAscent() &&
                    physicalLayout.getDescent() == logicalLayout.getDescent() &&
                    physicalLayout.getLeading() == logicalLayout.getLeading() &&
                    physicalLayout.getAdvance() == logicalLayout.getAdvance() &&
                    physicalLayout.getVisibleAdvance() == logicalLayout.getVisibleAdvance()) {
                physicalFonts.add(physicalFont);
            }
        }
        if (physicalFonts.isEmpty()) {
            return logicalFont;
        }
        return physicalFonts.poll();
    }

    public static Font getPhysicalFont(Font logicalFont) {
        return getPhysicalFont(logicalFont, FONT_TEST_STRING);
    }

    private static boolean isLogicalFontFamily(String family) {
        return (Font.DIALOG.equals(family) ||
                Font.DIALOG_INPUT.equals(family) ||
                Font.SANS_SERIF.equals(family) ||
                Font.SERIF.equals(family) ||
                Font.MONOSPACED.equals(family));
    }

    private static final FontRenderContext FONT_RENDER_CONTEXT =
            new FontRenderContext(null, false, true);

    private static final String FONT_TEST_STRING =
            "Falsches Üben von Xylophonmusik quält jeden größeren Zwerg";

    private static final FontExpressivenessComparator FONT_EXPRESSIVENESS_COMPARATOR =
            new FontExpressivenessComparator();

    private static class FontExpressivenessComparator implements Comparator<Font> {
        private static final int[] STYLES = {
                Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC
        };

        public int compare(Font font1, Font font2) {
            if (font1 == font2) {
                return 0;
            }
            Set<String> variantNames1 = new HashSet<String>();
            Set<String> variantNames2 = new HashSet<String>();
            for (int style : STYLES) {
                variantNames1.add(font1.deriveFont(style).getPSName());
                variantNames2.add(font2.deriveFont(style).getPSName());
            }
            if (variantNames1.size() < variantNames2.size()) {
                return 1;
            } else if (variantNames1.size() > variantNames2.size()) {
                return -1;
            }
            return font1.getName().compareTo(font2.getName());
        }
    }
}

