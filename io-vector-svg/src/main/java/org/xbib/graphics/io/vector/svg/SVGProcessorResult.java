package org.xbib.graphics.io.vector.svg;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.xbib.graphics.io.vector.GraphicsState;
import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.commands.AffineTransformCommand;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.commands.CreateCommand;
import org.xbib.graphics.io.vector.commands.DisposeCommand;
import org.xbib.graphics.io.vector.commands.DrawImageCommand;
import org.xbib.graphics.io.vector.commands.DrawShapeCommand;
import org.xbib.graphics.io.vector.commands.DrawStringCommand;
import org.xbib.graphics.io.vector.commands.FillShapeCommand;
import org.xbib.graphics.io.vector.commands.Group;
import org.xbib.graphics.io.vector.commands.SetBackgroundCommand;
import org.xbib.graphics.io.vector.commands.SetClipCommand;
import org.xbib.graphics.io.vector.commands.SetColorCommand;
import org.xbib.graphics.io.vector.commands.SetCompositeCommand;
import org.xbib.graphics.io.vector.commands.SetFontCommand;
import org.xbib.graphics.io.vector.commands.SetHintCommand;
import org.xbib.graphics.io.vector.commands.SetPaintCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.PageSize;
import org.xbib.graphics.io.vector.svg.util.Base64EncodeStream;
import org.xbib.graphics.io.vector.svg.util.VectorHints;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SVGProcessorResult implements ProcessorResult {

    private static final String SVG_DOCTYPE_QNAME = "svg";

    private static final String SVG_DOCTYPE_PUBLIC_ID = "-//W3C//DTD SVG 1.1//EN";

    private static final String SVG_DOCTYPE_SYSTEM_ID = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd";

    private static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";

    private static final String XLINK_NAMESPACE = "xlink";

    private static final String XLINK_NAMESPACE_URI = "http://www.w3.org/1999/xlink";

    private static final String PREFIX_CLIP = "clip";

    private static final String CHARSET = "UTF-8";

    private static final double DOTS_PER_MM = 2.834646; // 72 dpi

    //private static final double DOTS_PER_MM = 11.811024; // 300 dpi

    /**
     * Mapping of stroke endcap values from Java to SVG.
     */
    private static final Map<Integer, String> STROKE_ENDCAPS = Map.of(
                    BasicStroke.CAP_BUTT, "butt",
                    BasicStroke.CAP_ROUND, "round",
                    BasicStroke.CAP_SQUARE, "square"
    );
    /**
     * Mapping of line join values for path drawing from Java to SVG.
     */
    private static final Map<Integer, String> STROKE_LINEJOIN = Map.of(
            BasicStroke.JOIN_MITER, "miter",
            BasicStroke.JOIN_ROUND, "round",
            BasicStroke.JOIN_BEVEL, "bevel"
    );

    private final PageSize pageSize;

    private final Deque<GraphicsState> states;

    private final Document doc;

    private final Element root;

    private final Map<Integer, Element> clippingPathElements;

    private Element group;

    private boolean groupAdded;

    private Element defs;

    public SVGProcessorResult(PageSize pageSize) {
        this.pageSize = pageSize;
        states = new LinkedList<>();
        states.push(new GraphicsState());
        clippingPathElements = new HashMap<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setValidating(false);
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Could not create XML builder.");
        }
        DOMImplementation domImpl = docBuilder.getDOMImplementation();
        DocumentType docType = domImpl.createDocumentType(SVG_DOCTYPE_QNAME, SVG_DOCTYPE_PUBLIC_ID, SVG_DOCTYPE_SYSTEM_ID);
        doc = domImpl.createDocument(SVG_NAMESPACE_URI, "svg", docType);
        try {
            doc.setXmlStandalone(false);
        } catch (AbstractMethodError e) {
            throw new IllegalStateException("Your XML parser does not support standalone XML documents");
        }
        root = doc.getDocumentElement();
        initRoot();

        group = root;
    }

    private static void appendStyle(StringBuilder style, String attribute, Object value) {
        style.append(attribute).append(":")
                .append(format(value)).append(";");
    }

    private static String getOutput(AffineTransform tx) {
        StringBuilder out = new StringBuilder();
        if (AffineTransform.getTranslateInstance(tx.getTranslateX(), tx.getTranslateY()).equals(tx)) {
            out.append("translate(")
                    .append(format(tx.getTranslateX())).append(" ")
                    .append(format(tx.getTranslateY())).append(")");
        } else {
            double[] matrix = new double[6];
            tx.getMatrix(matrix);
            String s = Arrays.stream(matrix).mapToObj(String::valueOf).collect(Collectors.joining(" "));
            out.append("matrix(").append(s).append(")");
        }
        return out.toString();
    }

    private static String getOutput(Color color) {
        return String.format((Locale) null, "rgb(%d,%d,%d)",
                color.getRed(), color.getGreen(), color.getBlue());
    }

    private static String getOutput(Shape shape) {
        StringBuilder out = new StringBuilder();
        PathIterator segments = shape.getPathIterator(null);
        double[] coords = new double[6];
        for (int i = 0; !segments.isDone(); i++, segments.next()) {
            if (i > 0) {
                out.append(" ");
            }
            int segmentType = segments.currentSegment(coords);
            switch (segmentType) {
                case PathIterator.SEG_MOVETO:
                    out.append("M").append(coords[0]).append(",").append(coords[1]);
                    break;
                case PathIterator.SEG_LINETO:
                    out.append("L").append(coords[0]).append(",").append(coords[1]);
                    break;
                case PathIterator.SEG_CUBICTO:
                    out.append("C")
                            .append(coords[0]).append(",").append(coords[1]).append(" ")
                            .append(coords[2]).append(",").append(coords[3]).append(" ")
                            .append(coords[4]).append(",").append(coords[5]);
                    break;
                case PathIterator.SEG_QUADTO:
                    out.append("Q")
                            .append(coords[0]).append(",").append(coords[1]).append(" ")
                            .append(coords[2]).append(",").append(coords[3]);
                    break;
                case PathIterator.SEG_CLOSE:
                    out.append("Z");
                    break;
                default:
                    throw new IllegalStateException("Unknown path operation.");
            }
        }
        return out.toString();
    }

    private GraphicsState getCurrentState() {
        return states.peek();
    }

    private void initRoot() {
        double x = pageSize.getX();
        double y = pageSize.getY();
        double width = pageSize.getWidth();
        double height = pageSize.getHeight();
        root.setAttribute("xmlns:" + XLINK_NAMESPACE, XLINK_NAMESPACE_URI);
        root.setAttribute("version", "1.1");
        root.setAttribute("x", format(x / DOTS_PER_MM) + "mm");
        root.setAttribute("y", format(y / DOTS_PER_MM) + "mm");
        root.setAttribute("width", format(width / DOTS_PER_MM) + "mm");
        root.setAttribute("height", format(height / DOTS_PER_MM) + "mm");
        String s = Arrays.stream(new double[]{x, y, width, height}).mapToObj(String::valueOf).collect(Collectors.joining(" "));
        root.setAttribute("viewBox", s);
    }

    public void write(OutputStream out) throws IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        try {
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, CHARSET);
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                    doc.getDoctype().getPublicId());
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    doc.getDoctype().getSystemId());
            transformer.transform(new DOMSource(doc), new StreamResult(out));
        } catch (TransformerException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public void close() {
        // nothing to do
    }

    @Override
    public String toString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            write(out);
            return out.toString(CHARSET);
        } catch (IOException e) {
            return "";
        }
    }

    private void newGroup() {
        group = doc.createElement("g");
        groupAdded = false;
        Shape clip = getCurrentState().getClip();
        if (clip != GraphicsState.DEFAULT_CLIP) {
            Element clipElem = getClipElement(clip);
            String ref = "url(#" + clipElem.getAttribute("id") + ")";
            group.setAttribute("clip-path", ref);
        }
        AffineTransform tx = getCurrentState().getTransform();
        if (!GraphicsState.DEFAULT_TRANSFORM.equals(tx)) {
            group.setAttribute("transform", getOutput(tx));
        }
    }

    private Element getClipElement(Shape clip) {
        Element path = clippingPathElements.get(clip.hashCode());
        if (path != null) {
            return path;
        }
        if (defs == null) {
            defs = doc.createElement("defs");
            root.insertBefore(defs, root.getFirstChild());
        }
        path = doc.createElement("clipPath");
        path.setAttribute("id", PREFIX_CLIP + clip.hashCode());
        Element shape = getElement(clip);
        shape.removeAttribute("style");
        path.appendChild(shape);
        defs.appendChild(path);
        clippingPathElements.put(clip.hashCode(), path);
        return path;
    }

    private void addToGroup(Element e) {
        group.appendChild(e);
        if (!groupAdded && group != root) {
            root.appendChild(group);
            groupAdded = true;
        }
    }

    public void handle(Command<?> command) {
        if (command instanceof Group) {
            Group c = (Group) command;
            applyStateCommands(c.getValue());
            if (containsGroupCommand(c.getValue())) {
                newGroup();
            }
        } else if (command instanceof DrawImageCommand) {
            DrawImageCommand c = (DrawImageCommand) command;
            Element e = getElement(c.getValue(),
                    c.getX(), c.getY(), c.getWidth(), c.getHeight());
            addToGroup(e);
        } else if (command instanceof DrawShapeCommand) {
            DrawShapeCommand c = (DrawShapeCommand) command;
            Element e = getElement(c.getValue());
            e.setAttribute("style", getStyle(false));
            addToGroup(e);
        } else if (command instanceof DrawStringCommand) {
            DrawStringCommand c = (DrawStringCommand) command;
            Element e = getElement(c.getValue(), c.getX(), c.getY());
            e.setAttribute("style", getStyle(getCurrentState().getFont()));
            addToGroup(e);
        } else if (command instanceof FillShapeCommand) {
            FillShapeCommand c = (FillShapeCommand) command;
            Element e = getElement(c.getValue());
            e.setAttribute("style", getStyle(true));
            addToGroup(e);
        }
    }

    private void applyStateCommands(List<Command<?>> commands) {
        for (Command<?> command : commands) {
            GraphicsState state = getCurrentState();
            if (command instanceof SetBackgroundCommand) {
                SetBackgroundCommand c = (SetBackgroundCommand) command;
                state.setBackground(c.getValue());
            } else if (command instanceof SetClipCommand) {
                SetClipCommand c = (SetClipCommand) command;
                state.setClip(c.getValue());
            } else if (command instanceof SetColorCommand) {
                SetColorCommand c = (SetColorCommand) command;
                state.setColor(c.getValue());
            } else if (command instanceof SetCompositeCommand) {
                SetCompositeCommand c = (SetCompositeCommand) command;
                state.setComposite(c.getValue());
            } else if (command instanceof SetFontCommand) {
                SetFontCommand c = (SetFontCommand) command;
                state.setFont(c.getValue());
            } else if (command instanceof SetPaintCommand) {
                SetPaintCommand c = (SetPaintCommand) command;
                state.setPaint(c.getValue());
            } else if (command instanceof SetStrokeCommand) {
                SetStrokeCommand c = (SetStrokeCommand) command;
                state.setStroke(c.getValue());
            } else if (command instanceof SetTransformCommand) {
                SetTransformCommand c = (SetTransformCommand) command;
                state.setTransform(c.getValue());
            } else if (command instanceof AffineTransformCommand) {
                AffineTransformCommand c = (AffineTransformCommand) command;
                AffineTransform stateTransform = state.getTransform();
                AffineTransform transformToBeApplied = c.getValue();
                stateTransform.concatenate(transformToBeApplied);
                state.setTransform(stateTransform);
            } else if (command instanceof SetHintCommand) {
                SetHintCommand c = (SetHintCommand) command;
                state.getHints().put(c.getHintKey(), c.getValue());
            } else if (command instanceof CreateCommand) {
                try {
                    states.push((GraphicsState) getCurrentState().clone());
                } catch (CloneNotSupportedException e) {
                   // ignore
                }
            } else if (command instanceof DisposeCommand) {
                states.pop();
            }
        }
    }

    private boolean containsGroupCommand(List<Command<?>> commands) {
        for (Command<?> command : commands) {
            if ((command instanceof SetClipCommand) ||
                    (command instanceof SetTransformCommand) ||
                    (command instanceof AffineTransformCommand)) {
                return true;
            }
        }
        return false;
    }

    private String getStyle(boolean filled) {
        StringBuilder style = new StringBuilder();
        Color color = getCurrentState().getColor();
        String colorOutput = getOutput(color);
        double opacity = color.getAlpha() / 255.0;
        if (filled) {
            appendStyle(style, "fill", colorOutput);
            if (color.getAlpha() < 255) {
                appendStyle(style, "fill-opacity", opacity);
            }
        } else {
            appendStyle(style, "fill", "none");
        }
        if (!filled) {
            appendStyle(style, "stroke", colorOutput);
            if (color.getAlpha() < 255) {
                appendStyle(style, "stroke-opacity", opacity);
            }
            Stroke stroke = getCurrentState().getStroke();
            if (stroke instanceof BasicStroke) {
                BasicStroke bs = (BasicStroke) stroke;
                if (bs.getLineWidth() != 1f) {
                    appendStyle(style, "stroke-width", bs.getLineWidth());
                }
                if (bs.getMiterLimit() != 4f) {
                    appendStyle(style, "stroke-miterlimit", bs.getMiterLimit());
                }
                if (bs.getEndCap() != BasicStroke.CAP_BUTT) {
                    appendStyle(style, "stroke-linecap", STROKE_ENDCAPS.get(bs.getEndCap()));
                }
                if (bs.getLineJoin() != BasicStroke.JOIN_MITER) {
                    appendStyle(style, "stroke-linejoin", STROKE_LINEJOIN.get(bs.getLineJoin()));
                }
                if (bs.getDashArray() != null) {
                    float[] f = bs.getDashArray();
                    String s = IntStream.range(0, f.length)
                            .mapToDouble(i -> f[i])
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(","));
                    appendStyle(style, "stroke-dasharray", s);
                    if (bs.getDashPhase() != 0f) {
                        appendStyle(style, "stroke-dashoffset", bs.getDashPhase());
                    }
                }
            }
        } else {
            appendStyle(style, "stroke", "none");
        }
        return style.toString();
    }

    private String getStyle(Font font) {
        String style = getStyle(true);
        if (!GraphicsState.DEFAULT_FONT.equals(font)) {
            style += getOutput(font);
        }
        return style;
    }

    private Element getElement(Shape shape) {
        Element elem;
        if (shape instanceof Line2D) {
            Line2D s = (Line2D) shape;
            elem = doc.createElement("line");
            elem.setAttribute("x1", format(s.getX1()));
            elem.setAttribute("y1", format(s.getY1()));
            elem.setAttribute("x2", format(s.getX2()));
            elem.setAttribute("y2", format(s.getY2()));
        } else if (shape instanceof Rectangle2D) {
            Rectangle2D s = (Rectangle2D) shape;
            elem = doc.createElement("rect");
            elem.setAttribute("x", format(s.getX()));
            elem.setAttribute("y", format(s.getY()));
            elem.setAttribute("width", format(s.getWidth()));
            elem.setAttribute("height", format(s.getHeight()));
        } else if (shape instanceof RoundRectangle2D) {
            RoundRectangle2D s = (RoundRectangle2D) shape;
            elem = doc.createElement("rect");
            elem.setAttribute("x", format(s.getX()));
            elem.setAttribute("y", format(s.getY()));
            elem.setAttribute("width", format(s.getWidth()));
            elem.setAttribute("height", format(s.getHeight()));
            elem.setAttribute("rx", format(s.getArcWidth() / 2.0));
            elem.setAttribute("ry", format(s.getArcHeight() / 2.0));
        } else if (shape instanceof Ellipse2D) {
            Ellipse2D s = (Ellipse2D) shape;
            elem = doc.createElement("ellipse");
            elem.setAttribute("cx", format(s.getCenterX()));
            elem.setAttribute("cy", format(s.getCenterY()));
            elem.setAttribute("rx", format(s.getWidth() / 2.0));
            elem.setAttribute("ry", format(s.getHeight() / 2.0));
        } else {
            elem = doc.createElement("path");
            elem.setAttribute("d", getOutput(shape));
        }
        return elem;
    }

    private Element getElement(String text, double x, double y) {
        Element elem = doc.createElement("text");
        elem.appendChild(doc.createTextNode(text));
        elem.setAttribute("x", format(x));
        elem.setAttribute("y", format(y));
        return elem;
    }

    private Element getElement(Image image, double x, double y, double width, double height) {
        Element elem = doc.createElement("image");
        elem.setAttribute("x", format(x));
        elem.setAttribute("y", format(y));
        elem.setAttribute("width", format(width));
        elem.setAttribute("height", format(height));
        elem.setAttribute("preserveAspectRatio", "none");
        boolean lossyAllowed = getCurrentState().getHints().get(VectorHints.KEY_EXPORT) ==
                VectorHints.VALUE_EXPORT_SIZE;
        elem.setAttribute("xlink:href", getOutput(image, lossyAllowed));
        return elem;
    }

    private static String getOutput(Font font) {
        StringBuilder out = new StringBuilder();
        if (!GraphicsState.DEFAULT_FONT.getFamily().equals(font.getFamily())) {
            String physicalFamily = getPhysicalFont(font).getFamily();
            out.append("font-family:\"").append(physicalFamily).append("\";");
        }
        if (font.getSize2D() != GraphicsState.DEFAULT_FONT.getSize2D()) {
            out.append("font-size:").append(format(font.getSize2D())).append("px;");
        }
        if ((font.getStyle() & Font.ITALIC) != 0) {
            out.append("font-style:italic;");
        }
        if ((font.getStyle() & Font.BOLD) != 0) {
            out.append("font-weight:bold;");
        }
        return out.toString();
    }

    private static String getOutput(Image image, boolean lossyAllowed) {
        BufferedImage bufferedImage = toBufferedImage(image);
        String encoded = encodeImage(bufferedImage, "png");
        if (!usesAlpha(bufferedImage) && lossyAllowed) {
            String encodedLossy = encodeImage(bufferedImage, "jpeg");
            if (encodedLossy.length() > 0 && encodedLossy.length() < encoded.length()) {
                encoded = encodedLossy;
            }
        }
        return encoded;
    }

    /**
     * This method returns {@code true} if the specified image has at least one
     * pixel that is not fully opaque.
     *
     * @param image Image that should be checked for non-opaque pixels.
     * @return {@code true} if the specified image has transparent pixels,
     * {@code false} otherwise
     */
    private static boolean usesAlpha(Image image) {
        if (image == null) {
            return false;
        }
        BufferedImage bimage = toBufferedImage(image);
        Raster alphaRaster = bimage.getAlphaRaster();
        if (alphaRaster == null) {
            return false;
        }
        DataBuffer dataBuffer = alphaRaster.getDataBuffer();
        for (int i = 0; i < dataBuffer.getSize(); i++) {
            int alpha = dataBuffer.getElem(i);
            if (alpha < 255) {
                return true;
            }
        }
        return false;
    }

    private static String encodeImage(BufferedImage bufferedImage, String format) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        Base64EncodeStream encodeStream = new Base64EncodeStream(byteStream);
        try {
            ImageIO.write(bufferedImage, format, encodeStream);
            encodeStream.close();
            String encoded = byteStream.toString(StandardCharsets.ISO_8859_1);
            return String.format("data:image/%s;base64,%s", format, encoded);
        } catch (IOException e) {
            return "";
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
     * Returns a formatted string of the specified object.
     *
     * @param obj Object to convert to a string.
     * @return A formatted string.
     */
    private static String format(Object obj) {
        if (obj instanceof Number) {
            return format((Number) obj);
        } else {
            return obj.toString();
        }
    }


    /**
     * This method returns a buffered image with the contents of an image.
     * Taken from http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
     *
     * @param image Image to be converted
     * @return a buffered image with the contents of the specified image
     */
    private static BufferedImage toBufferedImage(Image image) {
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
    public static Font getPhysicalFont(Font logicalFont, String testText) {
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
            Set<String> variantNames1 = new HashSet<>();
            Set<String> variantNames2 = new HashSet<>();
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

