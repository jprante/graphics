package org.xbib.graphics.chart.category;

import org.xbib.graphics.chart.legend.LegendRenderable;
import org.xbib.graphics.chart.legend.LegendRenderType;

public enum CategorySeriesRenderStyle implements LegendRenderable {

    Line(LegendRenderType.Line),

    Area(LegendRenderType.Line),

    Scatter(LegendRenderType.Scatter),

    Bar(LegendRenderType.BoxNoOutline),

    SteppedBar(LegendRenderType.Box),

    Stick(LegendRenderType.Line);

    private final LegendRenderType legendRenderType;

    CategorySeriesRenderStyle(LegendRenderType legendRenderType) {
        this.legendRenderType = legendRenderType;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return legendRenderType;
    }
}
