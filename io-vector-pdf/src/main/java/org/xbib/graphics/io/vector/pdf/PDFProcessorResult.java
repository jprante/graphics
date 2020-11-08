package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.GraphicsState;
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
import org.xbib.graphics.io.vector.commands.SetFontCommand;
import org.xbib.graphics.io.vector.commands.SetHintCommand;
import org.xbib.graphics.io.vector.commands.SetPaintCommand;
import org.xbib.graphics.io.vector.commands.SetStrokeCommand;
import org.xbib.graphics.io.vector.commands.SetTransformCommand;
import org.xbib.graphics.io.vector.pdf.util.FlateEncodeStream;
import org.xbib.graphics.io.vector.pdf.util.ImageDataStream;
import org.xbib.graphics.io.vector.pdf.util.PDFObject;
import org.xbib.graphics.io.vector.pdf.util.Payload;
import org.xbib.graphics.io.vector.pdf.util.Resources;
import org.xbib.graphics.io.vector.pdf.util.FormattingWriter;
import org.xbib.graphics.io.vector.PageSize;
import org.xbib.graphics.io.vector.pdf.util.SizePayload;
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
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;

public class PDFProcessorResult implements ProcessorResult {

    private static final String EOL = "\n";

    private static final String CHARSET = "ISO-8859-1";

    private static final String HEADER = "%PDF-1.4";

    private static final String FOOTER = "%%EOF";

    /**
     * Constant to convert values from millimeters to PDF units (1/72th inch).
     */
    private static final double MM_IN_UNITS = 72.0 / 25.4;

    /**
     * Mapping of stroke endcap values from Java to PDF.
     */
    private static final Map<Integer, Integer> STROKE_ENDCAPS = map(
            new Integer[]{BasicStroke.CAP_BUTT, BasicStroke.CAP_ROUND, BasicStroke.CAP_SQUARE},
            new Integer[]{0, 1, 2}
    );

    /**
     * Mapping of line join values for path drawing from Java to PDF.
     */
    private static final Map<Integer, Integer> STROKE_LINEJOIN = map(
            new Integer[]{BasicStroke.JOIN_MITER, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL},
            new Integer[]{0, 1, 2}
    );

    private final PageSize pageSize;

    private final List<PDFObject> objects;

    private final Map<PDFObject, Long> xref;

    private final Map<Integer, PDFObject> images;

    private final Deque<GraphicsState> states;

    private int objectIdCounter;

    private PDFObject contents;

    private Resources resources;

    private boolean transformed;

    private boolean compressed;

    public PDFProcessorResult(PageSize pageSize) throws IOException {
        this.pageSize = pageSize;
        states = new LinkedList<>();
        states.push(new GraphicsState());
        objects = new LinkedList<>();
        objectIdCounter = 1;
        xref = new HashMap<>();
        images = new HashMap<>();
        compressed = false; // disable compress by default
        initPage();
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    private static String toString(PDFObject obj) throws IOException {
        StringBuilder out = new StringBuilder();
        out.append(obj.id).append(" ").append(obj.version).append(" obj")
                .append(EOL);
        if (!obj.dict.isEmpty()) {
            out.append(serialize(obj.dict)).append(EOL);
        }
        if (obj.payload != null) {
            String content = new String(obj.payload.getBytes(), CHARSET);
            if (content.length() > 0) {
                if (obj.payload.isStream()) {
                    out.append("stream").append(EOL);
                }
                out.append(content);
                if (obj.payload.isStream()) {
                    out.append("endstream");
                }
                out.append(EOL);
            }
        }
        out.append("endobj");
        return out.toString();
    }

    private static String serialize(Object obj) {
        if (obj instanceof String) {
            return "/" + obj.toString();
        } else if (obj instanceof float[]) {
            return serialize(asList((float[]) obj));
        } else if (obj instanceof double[]) {
            return serialize(asList((double[]) obj));
        } else if (obj instanceof Object[]) {
            return serialize(Arrays.asList((Object[]) obj));
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder out = new StringBuilder();
            out.append("[");
            int i = 0;
            for (Object elem : list) {
                if (i++ > 0) {
                    out.append(" ");
                }
                out.append(serialize(elem));
            }
            out.append("]");
            return out.toString();
        } else if (obj instanceof Map) {
            Map<?, ?> dict = (Map<?, ?>) obj;
            StringBuilder out = new StringBuilder();
            out.append("<<").append(EOL);
            for (Map.Entry<?, ?> entry : dict.entrySet()) {
                String key = entry.getKey().toString();
                out.append(serialize(key)).append(" ");
                Object value = entry.getValue();
                out.append(serialize(value)).append(EOL);
            }
            out.append(">>");
            return out.toString();
        } else if (obj instanceof PDFObject) {
            PDFObject pdfObj = (PDFObject) obj;
            return pdfObj.id + " " + pdfObj.version + " R";
        } else {
            return format(obj);
        }
    }

    private static String getOutput(Color c) {
        StringBuilder out = new StringBuilder();
        String r = serialize(c.getRed() / 255.0);
        String g = serialize(c.getGreen() / 255.0);
        String b = serialize(c.getBlue() / 255.0);
        out.append(r).append(" ").append(g).append(" ").append(b).append(" rg ")
                .append(r).append(" ").append(g).append(" ").append(b).append(" RG");
        return out.toString();
    }

    private static String getOutput(Shape s) {
        StringBuilder out = new StringBuilder();
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
                    out.append(serialize(coordsCur[0])).append(" ")
                            .append(serialize(coordsCur[1])).append(" m");
                    pointPrev[0] = coordsCur[0];
                    pointPrev[1] = coordsCur[1];
                    break;
                case PathIterator.SEG_LINETO:
                    out.append(serialize(coordsCur[0])).append(" ")
                            .append(serialize(coordsCur[1])).append(" l");
                    pointPrev[0] = coordsCur[0];
                    pointPrev[1] = coordsCur[1];
                    break;
                case PathIterator.SEG_CUBICTO:
                    out.append(serialize(coordsCur[0])).append(" ")
                            .append(serialize(coordsCur[1])).append(" ")
                            .append(serialize(coordsCur[2])).append(" ")
                            .append(serialize(coordsCur[3])).append(" ")
                            .append(serialize(coordsCur[4])).append(" ")
                            .append(serialize(coordsCur[5])).append(" c");
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
                    out.append(serialize(x1)).append(" ")
                            .append(serialize(y1)).append(" ")
                            .append(serialize(x2)).append(" ")
                            .append(serialize(y2)).append(" ")
                            .append(serialize(x3)).append(" ")
                            .append(serialize(y3)).append(" c");
                    pointPrev[0] = x3;
                    pointPrev[1] = y3;
                    break;
                case PathIterator.SEG_CLOSE:
                    out.append("h");
                    break;
                default:
                    throw new IllegalStateException("Unknown path operation.");
            }
        }

        return out.toString();
    }

    private static String getOutput(GraphicsState state, Resources resources, boolean first) {
        StringBuilder out = new StringBuilder();
        if (!first) {
            out.append("Q").append(EOL);
        }
        out.append("q").append(EOL);
        if (!state.getColor().equals(GraphicsState.DEFAULT_COLOR)) {
            if (state.getColor().getAlpha() != GraphicsState.DEFAULT_COLOR.getAlpha()) {
                double a = state.getColor().getAlpha() / 255.0;
                String resourceId = resources.getId(a);
                out.append("/").append(resourceId).append(" gs").append(EOL);
            }
            out.append(getOutput(state.getColor())).append(EOL);
        }
        if (!state.getTransform().equals(GraphicsState.DEFAULT_TRANSFORM)) {
            out.append(getOutput(state.getTransform())).append(" cm").append(EOL);
        }
        if (!state.getStroke().equals(GraphicsState.DEFAULT_STROKE)) {
            out.append(getOutput(state.getStroke())).append(EOL);
        }
        if (state.getClip() != GraphicsState.DEFAULT_CLIP) {
            out.append(getOutput(state.getClip())).append(" W n").append(EOL);
        }
        if (!state.getFont().equals(GraphicsState.DEFAULT_FONT)) {
            Font font = state.getFont();
            String fontResourceId = resources.getId(font);
            float fontSize = font.getSize2D();
            out.append("/").append(fontResourceId).append(" ").append(fontSize)
                    .append(" Tf").append(EOL);
        }
        return out.toString().replaceAll("(" + Pattern.quote(EOL) + ")+$", "");
    }

    private static String getOutput(Stroke s) {
        StringBuilder out = new StringBuilder();
        if (s instanceof BasicStroke) {
            BasicStroke strokeDefault = (BasicStroke) GraphicsState.DEFAULT_STROKE;
            BasicStroke strokeNew = (BasicStroke) s;
            if (strokeNew.getLineWidth() != strokeDefault.getLineWidth()) {
                out.append(serialize(strokeNew.getLineWidth()))
                        .append(" w").append(EOL);
            }
            if (strokeNew.getLineJoin() == BasicStroke.JOIN_MITER && strokeNew.getMiterLimit() != strokeDefault.getMiterLimit()) {
                out.append(serialize(strokeNew.getMiterLimit()))
                        .append(" M").append(EOL);
            }
            if (strokeNew.getLineJoin() != strokeDefault.getLineJoin()) {
                out.append(serialize(STROKE_LINEJOIN.get(strokeNew.getLineJoin())))
                        .append(" j").append(EOL);
            }
            if (strokeNew.getEndCap() != strokeDefault.getEndCap()) {
                out.append(serialize(STROKE_ENDCAPS.get(strokeNew.getEndCap())))
                        .append(" J").append(EOL);
            }
            if (strokeNew.getDashArray() != strokeDefault.getDashArray()) {
                if (strokeNew.getDashArray() != null) {
                    out.append(serialize(strokeNew.getDashArray())).append(" ")
                            .append(serialize(strokeNew.getDashPhase()))
                            .append(" d").append(EOL);
                } else {
                    out.append(EOL).append("[] 0 d").append(EOL);
                }
            }
        }
        return out.toString();
    }

    private static String getOutput(AffineTransform transform) {
        double[] matrix = new double[6];
        transform.getMatrix(matrix);
        return Arrays.stream(matrix).mapToObj(String::valueOf).collect(Collectors.joining(" "));
    }

    private static String getOutput(String str, double x, double y) {
        return "q " + "1 0 0 -1 " + x + " " + y + " cm " + "BT " + getOutput(str) + " Tj ET " + "Q";
    }

    private static StringBuilder getOutput(String str) {
        StringBuilder out = new StringBuilder();

        // Escape string
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

    private static String getOutput(PDFObject image, double x, double y,
                                    double width, double height, Resources resources) {
        String resourceId = resources.getId(image);
        return "q " + width + " 0 0 " + height + " " + x + " " + y + " cm " + "1 0 0 -1 0 1 cm " + "/" + resourceId + " Do " + "Q";
    }

    private GraphicsState getCurrentState() {
        return states.peek();
    }

    private void initPage() throws IOException {
        Map<String, Object> dict;
        dict = map(new String[]{"Type"}, new Object[]{"Catalog"});
        PDFObject catalog = addObject(dict, null);
        List<PDFObject> pagesKids = new LinkedList<>();
        dict = map(
                new String[]{"Type", "Kids", "Count"},
                new Object[]{"Pages", pagesKids, 1});
        PDFObject pages = addObject(dict, null);
        catalog.dict.put("Pages", pages);
        double x = pageSize.getX() * MM_IN_UNITS;
        double y = pageSize.getY() * MM_IN_UNITS;
        double width = pageSize.getWidth() * MM_IN_UNITS;
        double height = pageSize.getHeight() * MM_IN_UNITS;
        dict = map(
                new String[]{"Type", "Parent", "MediaBox"},
                new Object[]{"Page", pages, new double[]{x, y, width, height}});
        PDFObject page = addObject(dict, null);
        pagesKids.add(page);
        Payload contentsPayload = new Payload(true);
        contents = addObject(null, contentsPayload);
        page.dict.put("Contents", contents);
        if (compressed) {
            try {
                contentsPayload.addFilter(FlateEncodeStream.class);
                contents.dict.put("Filter", new Object[]{"FlateDecode"});
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                // ignore
            }
        }
        String s = String.join("", "q", EOL,
                getOutput(getCurrentState().getColor()), EOL,
                Double.toString(MM_IN_UNITS), " 0 0 ",
                Double.toString(-MM_IN_UNITS), " 0 ",
                Double.toString(height), " cm", EOL);
        contentsPayload.write(s.getBytes(CHARSET));
        Payload contentLengthPayload = new SizePayload(contents, CHARSET, false);
        PDFObject contentLength = addObject(null, contentLengthPayload);
        contents.dict.put("Length", contentLength);
        resources = new Resources(objectIdCounter++, 0);
        objects.add(resources);
        page.dict.put("Resources", resources);
        Font font = getCurrentState().getFont();
        String fontResourceId = resources.getId(font);
        float fontSize = font.getSize2D();
        contentsPayload.write(("/" + fontResourceId + " " + fontSize + " Tf" + EOL).getBytes(CHARSET));
    }

    private PDFObject addObject(Map<String, Object> dict, Payload payload) {
        final int id = objectIdCounter++;
        final int version = 0;
        PDFObject object = new PDFObject(id, version, dict, payload);
        objects.add(object);
        return object;
    }

    private PDFObject addObject(Image image) throws IOException {
        BufferedImage bufferedImage = toBufferedImage(image);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        int bitsPerSample = max(bufferedImage.getSampleModel().getSampleSize());
        int bands = bufferedImage.getSampleModel().getNumBands();
        String colorSpaceName = (bands == 1) ? "DeviceGray" : "DeviceRGB";
        Payload imagePayload = new Payload(true);
        String[] imageFilters = {};
        if (compressed) {
            try {
                imagePayload.addFilter(FlateEncodeStream.class);
                imageFilters = new String[]{"FlateDecode"};
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                // ignore
            }
        }
        InputStream imageDataStream = new ImageDataStream(bufferedImage, ImageDataStream.Interleaving.WITHOUT_ALPHA);
        imageDataStream.transferTo(imagePayload);
        imagePayload.close();
        int length = imagePayload.getBytes().length;
        Map<String, Object> imageDict = map(
                new String[]{"Type", "Subtype", "Width", "Height", "ColorSpace",
                        "BitsPerComponent", "Length", "Filter"},
                new Object[]{"XObject", "Image", width, height, colorSpaceName,
                        bitsPerSample, length, imageFilters}
        );
        PDFObject imageObject = addObject(imageDict, imagePayload);
        boolean hasAlpha = bufferedImage.getColorModel().hasAlpha();
        if (hasAlpha) {
            BufferedImage mask = getAlphaImage(bufferedImage);
            PDFObject maskObject = addObject(mask);
            boolean isBitmask = mask.getSampleModel().getSampleSize(0) == 1;
            if (isBitmask) {
                maskObject.dict.put("ImageMask", true);
                maskObject.dict.remove("ColorSpace");
                imageObject.dict.put("Mask", maskObject);
            } else {
                imageObject.dict.put("SMask", maskObject);
            }
        }
        return imageObject;
    }

    public void write(OutputStream out) throws IOException {
        FormattingWriter o = new FormattingWriter(out, CHARSET, EOL);
        o.writeln(HEADER);
        for (PDFObject obj : objects) {
            xref.put(obj, o.tell());
            o.writeln(toString(obj));
            o.flush();
        }
        long xrefPos = o.tell();
        o.writeln("xref");
        o.write(format(0)).write(" ").writeln(format(objects.size() + 1));
        o.format("%010d %05d f ", 0, 65535).writeln();
        for (PDFObject obj : objects) {
            o.format("%010d %05d n ", xref.get(obj), 0).writeln();
        }
        o.flush();
        o.writeln("trailer");
        o.writeln(serialize(map(
                new String[]{"Size", "Root"},
                new Object[]{objects.size() + 1, objects.get(0)}
        )));
        o.writeln("startxref");
        o.writeln(format(xrefPos));
        o.writeln(FOOTER);
        o.flush();
    }

    public void handle(Command<?> command) throws IOException {
        String s = "";
        if (command instanceof Group) {
            Group c = (Group) command;
            applyStateCommands(c.getValue());
            s = getOutput(getCurrentState(), resources, !transformed);
            transformed = true;
        } else if (command instanceof DrawShapeCommand) {
            DrawShapeCommand c = (DrawShapeCommand) command;
            s = getOutput(c.getValue()) + " S";
        } else if (command instanceof FillShapeCommand) {
            FillShapeCommand c = (FillShapeCommand) command;
            s = getOutput(c.getValue()) + " f";
        } else if (command instanceof DrawStringCommand) {
            DrawStringCommand c = (DrawStringCommand) command;
            s = getOutput(c.getValue(), c.getX(), c.getY());
        } else if (command instanceof DrawImageCommand) {
            DrawImageCommand c = (DrawImageCommand) command;
            // Create object for image data
            Image image = c.getValue();
            PDFObject imageObject = images.get(image.hashCode());
            if (imageObject == null) {
                imageObject = addObject(image);
                images.put(image.hashCode(), imageObject);
            }
            s = getOutput(imageObject, c.getX(), c.getY(),
                    c.getWidth(), c.getHeight(), resources);
        }
        Payload contentsPayload = contents.payload;
        contentsPayload.write(s.getBytes(CHARSET));
        contentsPayload.write(EOL.getBytes(CHARSET));
    }

    private void applyStateCommands(List<Command<?>> commands) {
        for (Command<?> command : commands) {
            if (command instanceof SetHintCommand) {
                SetHintCommand c = (SetHintCommand) command;
                getCurrentState().getHints().put(c.getHintKey(), c.getValue());
            } else if (command instanceof SetBackgroundCommand) {
                SetBackgroundCommand c = (SetBackgroundCommand) command;
                getCurrentState().setBackground(c.getValue());
            } else if (command instanceof SetColorCommand) {
                SetColorCommand c = (SetColorCommand) command;
                getCurrentState().setColor(c.getValue());
            } else if (command instanceof SetPaintCommand) {
                SetPaintCommand c = (SetPaintCommand) command;
                getCurrentState().setPaint(c.getValue());
            } else if (command instanceof SetStrokeCommand) {
                SetStrokeCommand c = (SetStrokeCommand) command;
                getCurrentState().setStroke(c.getValue());
            } else if (command instanceof SetFontCommand) {
                SetFontCommand c = (SetFontCommand) command;
                getCurrentState().setFont(c.getValue());
            } else if (command instanceof SetTransformCommand) {
                throw new UnsupportedOperationException("The PDF format has no means of setting the transformation matrix.");
            } else if (command instanceof AffineTransformCommand) {
                AffineTransformCommand c = (AffineTransformCommand) command;
                AffineTransform stateTransform = getCurrentState().getTransform();
                AffineTransform transformToBeApplied = c.getValue();
                stateTransform.concatenate(transformToBeApplied);
                getCurrentState().setTransform(stateTransform);
            } else if (command instanceof SetClipCommand) {
                SetClipCommand c = (SetClipCommand) command;
                getCurrentState().setClip(c.getValue());
            } else if (command instanceof CreateCommand) {
                try {
                    states.push((GraphicsState) getCurrentState().clone());
                } catch (CloneNotSupportedException e) {
                    // do nothing
                }
            } else if (command instanceof DisposeCommand) {
                states.pop();
            }
        }
    }

    @Override
    public void close() throws IOException {
        String footer = "Q" + EOL;
        if (transformed) {
            footer += "Q" + EOL;
        }
        Payload contentsPayload = contents.payload;
        contentsPayload.write(footer.getBytes(CHARSET));
        contentsPayload.close();
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
        // Fill map with keys and values
        Map<K, V> map = new LinkedHashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * Converts an array of {@code float} numbers to a list of {@code Float}s.
     * The list will be empty if the array is empty or {@code null}.
     *
     * @param elements Array of float numbers.
     * @return A list with all numbers as {@code Float}.
     */
    private static List<Float> asList(float[] elements) {
        int size = elements != null ? elements.length : 0;
        List<Float> list = new ArrayList<>(size);
        if (elements != null) {
            for (Float elem : elements) {
                list.add(elem);
            }
        }
        return list;
    }

    /**
     * Converts an array of {@code double} numbers to a list of {@code Double}s.
     * The list will be empty if the array is empty or {@code null}.
     *
     * @param elements Array of double numbers.
     * @return A list with all numbers as {@code Double}.
     */
    private static List<Double> asList(double[] elements) {
        int size = (elements != null) ? elements.length : 0;
        List<Double> list = new ArrayList<>(size);
        if (elements != null) {
            for (Double elem : elements) {
                list.add(elem);
            }
        }
        return list;
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
     * Returns the largest of all specified values.
     *
     * @param values Several integer values.
     * @return largest value.
     */
    public static int max(int... values) {
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

    private static BufferedImage getAlphaImage(BufferedImage image) {
        WritableRaster alphaRaster = image.getAlphaRaster();
        int width = image.getWidth();
        int height = image.getHeight();
        ColorModel cm;
        WritableRaster raster;
        ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        int[] bits = {8};
        cm = new ComponentColorModel(colorSpace, bits, false, true,
                Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        raster = cm.createCompatibleWritableRaster(width, height);
        BufferedImage alphaImage = new BufferedImage(cm, raster, false, null);
        int[] alphaValues = new int[image.getWidth() * alphaRaster.getNumBands()];
        for (int y = 0; y < image.getHeight(); y++) {
            alphaRaster.getPixels(0, y, image.getWidth(), 1, alphaValues);
            if (image.getTransparency() == BufferedImage.BITMASK) {
                for (int i = 0; i < alphaValues.length; i++) {
                    if (alphaValues[i] > 0) {
                        alphaValues[i] = 255;
                    }
                }
            }
            alphaImage.getRaster().setPixels(0, y, image.getWidth(), 1, alphaValues);
        }
        return alphaImage;
    }
}
