package org.xbib.graphics.chart;

import org.xbib.graphics.chart.axis.Direction;
import org.xbib.graphics.chart.axis.Axis;
import org.xbib.graphics.chart.axis.AxisPair;
import org.xbib.graphics.chart.io.BitmapFormat;
import org.xbib.graphics.chart.io.VectorGraphicsFormat;
import org.xbib.graphics.chart.legend.Legend;
import org.xbib.graphics.chart.plot.Plot;
import org.xbib.graphics.chart.series.Series;
import org.xbib.graphics.chart.style.Styler;
import org.xbib.graphics.chart.io.vector.EPSGraphics2D;
import org.xbib.graphics.chart.io.vector.PDFGraphics2D;
import org.xbib.graphics.chart.io.vector.ProcessingPipeline;
import org.xbib.graphics.chart.io.vector.SVGGraphics2D;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

public abstract class Chart<ST extends Styler, S extends Series> {

    protected final ST styler;

    protected final ChartTitle<ST, S> chartTitle;

    protected final Map<String, S> seriesMap;

    protected AxisPair<?, ?> axisPair;

    protected Plot<ST, S> plot;

    protected Legend<ST, S> legend;

    private int width;

    private int height;

    private String title = "";

    private String xAxisTitle = "";

    private String yAxisTitle = "";

    private final Map<Integer, String> yAxisGroupTitleMap;

    protected Chart(int width, int height, ST styler) {
        this.styler = styler;
        this.width = width;
        this.height = height;
        this.chartTitle = new ChartTitle<>(this);
        this.seriesMap = new LinkedHashMap<>();
        this.yAxisGroupTitleMap = new HashMap<>();
    }

    public abstract void paint(Graphics2D g, int width, int height);

    protected void paintBackground(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, styler.getAntiAlias()
                        ? RenderingHints.VALUE_ANTIALIAS_ON
                        : RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(styler.getChartBackgroundColor());
        Shape rect = new Rectangle2D.Double(0, 0, getWidth(), getHeight());
        g.fill(rect);
    }

    public List<Double> listFromDoubleArray(double[] data) {
        if (data == null) {
            return null;
        }
        List<Double> dataNumber;
        dataNumber = new ArrayList<>();
        for (double d : data) {
            dataNumber.add(d);
        }
        return dataNumber;
    }

    public List<Double> listFromFloatArray(float[] data) {
        if (data == null) {
            return null;
        }
        List<Double> dataNumber;
        dataNumber = new ArrayList<>();
        for (float f : data) {
            dataNumber.add((double) f);
        }
        return dataNumber;
    }

    public List<Double> listFromIntArray(int[] data) {
        if (data == null) {
            return null;
        }
        List<Double> dataNumber;
        dataNumber = new ArrayList<>();
        for (double d : data) {
            dataNumber.add(d);
        }
        return dataNumber;
    }

    public List<Double> getGeneratedData(int length) {
        List<Double> generatedData = new ArrayList<>();
        for (int i = 1; i < length + 1; i++) {
            generatedData.add((double) i);
        }
        return generatedData;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getXAxisTitle() {
        return xAxisTitle;
    }

    public void setXAxisTitle(String xAxisTitle) {
        this.xAxisTitle = xAxisTitle;
    }

    public String getyYAxisTitle() {
        return yAxisTitle;
    }

    public void setYAxisTitle(String yAxisTitle) {
        this.yAxisTitle = yAxisTitle;
    }

    public String getYAxisGroupTitle(int yAxisGroup) {
        String title = yAxisGroupTitleMap.get(yAxisGroup);
        if (title == null) {
            return yAxisTitle;
        }
        return title;
    }

    public void setYAxisGroupTitle(int yAxisGroup, String yAxisTitle) {
        yAxisGroupTitleMap.put(yAxisGroup, yAxisTitle);
    }

    public void setXAxisLabelOverrideMap(Map<Double, Object> overrideMap) {
        axisPair.getAxisLabelOverrideMap().put("X0", overrideMap);
    }

    public void setYAxisLabelOverrideMap(Map<Double, Object> overrideMap) {
        axisPair.getAxisLabelOverrideMap().put("Y0", overrideMap);
    }

    public void setYAxisLabelOverrideMap(Map<Double, Object> overrideMap, int yAxisGroup) {
        axisPair.getAxisLabelOverrideMap().put(("Y" + yAxisGroup), overrideMap);
    }

    public Map<Double, Object> getYAxisLabelOverrideMap(Direction direction, int yIndex) {
        Map<String, Map<Double, Object>> axisLabelOverrideMap = axisPair.getAxisLabelOverrideMap();
        return axisLabelOverrideMap.get((direction.name() + yIndex));
    }

    public ChartTitle<ST, S> getChartTitle() {
        return chartTitle;
    }

    public Legend<ST, S> getLegend() {
        return legend;
    }

    public Plot<ST, S> getPlot() {
        return plot;
    }

    public Axis<?, ?> getXAxis() {
        return axisPair.getXAxis();
    }

    public Axis<?, ?> getYAxis() {
        return axisPair.getYAxis();
    }

    public Axis<?, ?> getYAxis(int yIndex) {
        return axisPair.getYAxis(yIndex);
    }

    public AxisPair<?, ?> getAxisPair() {
        return axisPair;
    }

    public Map<String, S> getSeriesMap() {
        return seriesMap;
    }

    public S removeSeries(String seriesName) {
        return seriesMap.remove(seriesName);
    }

    public ST getStyler() {
        return styler;
    }

    public Format getXAxisFormat() {
        return axisPair.getXAxis().getAxisTickCalculator().getAxisFormat();
    }

    public Format getYAxisFormat() {
        return axisPair.getYAxis().getAxisTickCalculator().getAxisFormat();
    }

    /**
     * Save chart as an image file.
     *
     * @param outputStream output stream
     * @param bitmapFormat bitmap format
     * @throws IOException  if save fails
     */
    public void saveBitmap(OutputStream outputStream, BitmapFormat bitmapFormat) throws IOException {
        BufferedImage bufferedImage = getBufferedImage();
        ImageIO.write(bufferedImage, bitmapFormat.toString().toLowerCase(), outputStream);
        outputStream.close();
    }

    /**
     * Save a chart as a PNG with a custom DPI. The default DPI is 72, which is fine for displaying charts on a
     * computer
     * monitor, but for printing
     * charts, a DPI of around 300 is much better.
     *
     * @param outputStream output stream
     * @param dpi dot sper inch
     * @throws IOException if save fails
     */
    public void saveBitmapWithDPI(OutputStream outputStream, BitmapFormat bitmapFormat, int dpi) throws IOException {
        double scaleFactor = dpi / 72.0;
        BufferedImage bufferedImage = new BufferedImage((int) (getWidth() * scaleFactor), (int) (getHeight() * scaleFactor), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        AffineTransform at = graphics2D.getTransform();
        at.scale(scaleFactor, scaleFactor);
        graphics2D.setTransform(at);
        paint(graphics2D, getWidth(), getHeight());
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(bitmapFormat.toString().toLowerCase());
        if (writers.hasNext()) {
            ImageWriter writer = writers.next();
            // instantiate an ImageWriteParam object with default compression options
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, iwp);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                throw new IllegalArgumentException("It is not possible to set the DPI on a bitmap with " + bitmapFormat + " format!! Try another format.");
            }
            setDPI(metadata, dpi);
            try {
                writer.setOutput(outputStream);
                IIOImage image = new IIOImage(bufferedImage, null, metadata);
                writer.write(null, image, iwp);
            } finally {
                writer.dispose();
            }
        }
    }

    /**
     * Sets the metadata.
     *
     * @param metadata metadata
     * @param DPI dots per inch
     * @throws IIOInvalidTreeException if setting fails
     */
    private static void setDPI(IIOMetadata metadata, int DPI) throws IIOInvalidTreeException {
        // for PNG, it's dots per millimeter
        double dotsPerMilli = 1.0 * DPI / 10 / 2.54;
        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));
        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));
        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);
        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);
        metadata.mergeTree("javax_imageio_1.0", root);
    }

    /**
     * Save a Chart as a JPEG file
     *
     * @param outputStream output stream
     * @param quality  - a float between 0 and 1 (1 = maximum quality)
     * @throws IOException if save fails
     */
    public void saveJPGWithQuality(OutputStream outputStream, float quality) throws IOException {
        BufferedImage bufferedImage = getBufferedImage();
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
        ImageWriter writer = iter.next();
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);
        try (outputStream) {
            writer.setOutput(outputStream);
            IIOImage image = new IIOImage(bufferedImage, null, null);
            writer.write(null, image, iwp);
            writer.dispose();
        }
    }

    /**
     * Generates a byte[] for a given chart.
     *
     * @param bitmapFormat bitmap format
     * @return a byte[] for a given chart
     * @throws IOException if byte array fails
     */
    public byte[] getBitmapBytes(BitmapFormat bitmapFormat) throws IOException {
        BufferedImage bufferedImage = getBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, bitmapFormat.toString().toLowerCase(), baos);
        baos.flush();
        byte[] imageInBytes = baos.toByteArray();
        baos.close();
        return imageInBytes;
    }

    public BufferedImage getBufferedImage() {
        BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        paint(graphics2D, getWidth(), getHeight());
        return bufferedImage;
    }

    public void write(OutputStream outputStream, VectorGraphicsFormat vectorGraphicsFormat)
            throws IOException {
        ProcessingPipeline g = null;
        switch (vectorGraphicsFormat) {
            case EPS:
                g = new EPSGraphics2D(0.0, 0.0, getWidth(), getHeight());
                break;
            case PDF:
                g = new PDFGraphics2D(0.0, 0.0, getWidth(), getHeight());
                break;
            case SVG:
                g = new SVGGraphics2D(0.0, 0.0, getWidth(), getHeight());
                break;

            default:
                break;
        }
        paint(g, getWidth(), getHeight());
        if (outputStream != null) {
            outputStream.write(g.getBytes());
        }
    }
}
