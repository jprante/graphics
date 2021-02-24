package org.xbib.graphics.chart.category;

import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.theme.Theme;

public class CategoryStyler extends AxesChartStyler {

    private CategorySeriesRenderStyle categorySeriesRenderStyle;

    private double availableSpaceFill;
    private boolean isOverlapped;
    private boolean isStacked;

    public CategoryStyler() {

        this.setAllStyles();
        super.setAllStyles();
    }

    @Override
    protected void setAllStyles() {

        this.categorySeriesRenderStyle = CategorySeriesRenderStyle.Bar; // set default to bar

        availableSpaceFill = theme.getAvailableSpaceFill();
        isOverlapped = theme.isOverlapped();
    }

    /**
     * Sets the available space for rendering each category as a percentage. For a bar chart with one
     * series, it will be the width of the bar as a percentage of the maximum space alloted for the
     * bar. If there are three series and three bars, the three bars will share the available space.
     * This affects all category series render types, not only bar charts. Full width is 100%, i.e.
     * 1.0
     *
     * @param availableSpaceFill space fill
     */
    public void setAvailableSpaceFill(double availableSpaceFill) {
        this.availableSpaceFill = availableSpaceFill;
    }

    public double getAvailableSpaceFill() {

        return availableSpaceFill;
    }

    /**
     * Sets the default series render style for the chart (bar, stick, line, scatter, area, etc.) You can override the
     * series render style individually on each Series object.
     *
     * @param categorySeriesRenderStyle render style
     */
    public void setDefaultSeriesRenderStyle(CategorySeriesRenderStyle categorySeriesRenderStyle) {
        this.categorySeriesRenderStyle = categorySeriesRenderStyle;
    }

    public CategorySeriesRenderStyle getDefaultSeriesRenderStyle() {
        return categorySeriesRenderStyle;
    }

    public boolean isOverlapped() {
        return isOverlapped;
    }

    /**
     * set whether or no bars are overlapped. Otherwise they are places side-by-side
     *
     * @param isOverlapped overlapped
     */
    public void setOverlapped(boolean isOverlapped) {
        this.isOverlapped = isOverlapped;
    }

    public boolean isStacked() {
        return isStacked;
    }

    /**
     * Set whether or not series renderings (i.e. bars, stick, etc.) are stacked.
     *
     * @param isStacked stacked
     */
    public void setStacked(boolean isStacked) {
        this.isStacked = isStacked;
    }

    /**
     * Set the theme the styler should use
     *
     * @param theme theme
     */
    protected void setTheme(Theme theme) {
        this.theme = theme;
        super.setAllStyles();
    }
}
