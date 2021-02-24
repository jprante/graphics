package org.xbib.graphics.chart.xy;

import org.xbib.graphics.chart.style.AxesChartStyler;
import org.xbib.graphics.chart.theme.Theme;

public class XYStyler extends AxesChartStyler {

    private XYSeriesRenderStyle xySeriesRenderStyle;

    public XYStyler() {
        this.setAllStyles();
        super.setAllStyles();
    }

    @Override
    protected void setAllStyles() {
        xySeriesRenderStyle = XYSeriesRenderStyle.Line; // set default to line
    }

    public XYSeriesRenderStyle getDefaultSeriesRenderStyle() {
        return xySeriesRenderStyle;
    }

    /**
     * Sets the default series render style for the chart (line, scatter, area, etc.) You can override the series
     * render
     * style individually on each Series object.
     *
     * @param style style
     */
    public void setDefaultSeriesRenderStyle(XYSeriesRenderStyle style) {
        this.xySeriesRenderStyle = style;
    }

    public Theme getTheme() {
        return theme;
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
