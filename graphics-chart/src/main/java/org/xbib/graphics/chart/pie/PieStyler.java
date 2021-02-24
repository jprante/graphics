package org.xbib.graphics.chart.pie;

import org.xbib.graphics.chart.style.Styler;
import org.xbib.graphics.chart.theme.Theme;

import java.awt.Font;

public class PieStyler extends Styler {

    private PieSeriesRenderStyle pieSeriesRenderStyle;
    private boolean isCircular;
    private double startAngleInDegrees;
    private Font annotationFont;
    private double annotationDistance;
    private AnnotationType annotationType;
    private boolean drawAllAnnotations;
    private double donutThickness;
    private boolean isSumVisible;
    private Font sumFont;

    public PieStyler() {
        this.setAllStyles();
        super.setAllStyles();
    }

    @Override
    protected void setAllStyles() {
        this.pieSeriesRenderStyle = PieSeriesRenderStyle.Pie;
        this.isCircular = theme.isCircular();
        this.annotationFont = theme.getPieFont();
        this.annotationDistance = theme.getAnnotationDistance();
        this.annotationType = theme.getAnnotationType();
        this.drawAllAnnotations = theme.isDrawAllAnnotations();
        this.donutThickness = theme.getDonutThickness();
        this.hasAnnotations = true;
        this.isSumVisible = theme.isSumVisible();
        this.sumFont = theme.getSumFont();
    }

    public PieSeriesRenderStyle getDefaultSeriesRenderStyle() {
        return pieSeriesRenderStyle;
    }

    /**
     * Sets the default series render style for the chart (line, scatter, area, etc.) You can override the series
     * render
     * style individually on each Series object.
     *
     * @param pieSeriesRenderStyle render style
     */
    public void setDefaultSeriesRenderStyle(PieSeriesRenderStyle pieSeriesRenderStyle) {
        this.pieSeriesRenderStyle = pieSeriesRenderStyle;
    }

    public boolean isCircular() {
        return isCircular;
    }

    /**
     * Sets whether or not the pie chart is forced to be circular. Otherwise it's shape is oval, matching the
     * containing
     * plot.
     *
     * @param isCircular circular
     */
    public void setCircular(boolean isCircular) {
        this.isCircular = isCircular;
    }

    public double getStartAngleInDegrees() {
        return startAngleInDegrees;
    }

    /**
     * Sets the start angle in degrees. Zero degrees is straight up.
     *
     * @param startAngleInDegrees start angle in degrees
     */
    public void setStartAngleInDegrees(double startAngleInDegrees) {
        this.startAngleInDegrees = startAngleInDegrees;
    }

    /**
     * Sets the font used on the Pie Chart's annotations
     *
     * @param pieFont pie font
     */
    public void setAnnotationFont(Font pieFont) {
        this.annotationFont = pieFont;
    }

    public Font getAnnotationFont() {
        return annotationFont;
    }

    /**
     * Sets the distance of the pie chart's annotation where 0 is the center, 1 is at the edge and greater than 1 is
     * outside of the pie chart.
     *
     * @param annotationDistance annotation distance
     */
    public void setAnnotationDistance(double annotationDistance) {
        this.annotationDistance = annotationDistance;
    }

    public double getAnnotationDistance() {
        return annotationDistance;
    }

    /**
     * Sets the Pie chart's annotation type
     *
     * @param annotationType annotation type
     */
    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setDrawAllAnnotations(boolean drawAllAnnotations) {
        this.drawAllAnnotations = drawAllAnnotations;
    }

    public boolean isDrawAllAnnotations() {
        return drawAllAnnotations;
    }

    /**
     * Sets the thickness of the donut ring for donut style pie chart series.
     *
     * @param donutThickness - Valid range is between 0 and 1.
     */
    public void setDonutThickness(double donutThickness) {
        this.donutThickness = donutThickness;
    }

    public double getDonutThickness() {
        return donutThickness;
    }

    /**
     * Sets whether or not the sum is visible in the centre of the pie chart.
     *
     * @param isSumVisible sum visible
     */
    public void setSumVisible(boolean isSumVisible) {
        this.isSumVisible = isSumVisible;
    }

    public boolean isSumVisible() {
        return isSumVisible;
    }

    /**
     * Sets the font size for the sum.
     *
     * @param sumFontSize font size
     */
    public void setSumFontSize(float sumFontSize) {
        this.sumFont = this.sumFont.deriveFont(sumFontSize);
    }

    public Font getSumFont() {
        return sumFont;
    }

    /**
     * Sets the font for the sum.
     *
     * @param sumFont font
     */
    public void setSumFont(Font sumFont) {
        this.sumFont = sumFont;
    }

    /**
     * Set the theme the styler should use
     *
     * @param theme theme
     */
    public void setTheme(Theme theme) {
        this.theme = theme;
        super.setAllStyles();
    }

    public Theme getTheme() {
        return theme;
    }


    public enum AnnotationType {
        Value, Percentage, Label, LabelAndPercentage
    }

}
