package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.style.AxesChartStyler;

import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.text.Format;
import java.util.LinkedList;
import java.util.List;

public abstract class AxisTickCalculator {

    protected final Direction axisDirection;

    protected final double workingSpace;

    protected final double minValue;

    protected final double maxValue;

    protected final AxesChartStyler styler;

    protected Format axisFormat;

    protected List<Double> tickLocations = new LinkedList<>();

    protected List<String> tickLabels = new LinkedList<>();

    public AxisTickCalculator(Direction axisDirection, double workingSpace, double minValue, double maxValue, AxesChartStyler styler) {
        this.axisDirection = axisDirection;
        this.workingSpace = workingSpace;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.styler = styler;
    }

    /**
     * Gets the first position
     *
     * @param gridStep grid step
     * @return first posiition
     */
    public double getFirstPosition(double gridStep) {
        return minValue - (minValue % gridStep) - gridStep;
    }

    public List<Double> getTickLocations() {
        return tickLocations;
    }

    public List<String> getTickLabels() {
        return tickLabels;
    }

    /**
     * Given the generated tickLabels, will they fit side-by-side without overlapping each other and looking bad?
     * Sometimes the given tickSpacingHint is simply too small.
     *
     * @param tickLabels tick lables
     * @param tickSpacingHint tick psacing hint
     * @return true if it fits
     */
    public boolean willLabelsFitInTickSpaceHint(List<String> tickLabels, int tickSpacingHint) {
        if (this.axisDirection == Direction.Y) {
            return true;
        }
        String sampleLabel = " ";
        for (String tickLabel : tickLabels) {
            if (tickLabel != null && tickLabel.length() > sampleLabel.length()) {
                sampleLabel = tickLabel;
            }
        }
        TextLayout textLayout = new TextLayout(sampleLabel, styler.getAxisTickLabelsFont(), new FontRenderContext(null, true, false));
        AffineTransform rot = styler.getXAxisLabelRotation() == 0 ? null : AffineTransform.getRotateInstance(-1 * Math.toRadians(styler.getXAxisLabelRotation()));
        Shape shape = textLayout.getOutline(rot);
        Rectangle2D rectangle = shape.getBounds();
        double largestLabelWidth = rectangle.getWidth();
        return (largestLabelWidth * 1.1 < tickSpacingHint);
    }

    public Format getAxisFormat() {
        return axisFormat;
    }
}
