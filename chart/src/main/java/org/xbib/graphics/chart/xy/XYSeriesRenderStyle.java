package org.xbib.graphics.chart.xy;

import org.xbib.graphics.chart.legend.LegendRenderable;
import org.xbib.graphics.chart.legend.LegendRenderType;

public enum XYSeriesRenderStyle implements LegendRenderable {

    Line(LegendRenderType.Line),

    Area(LegendRenderType.Line),

    Step(LegendRenderType.Line),

    StepArea(LegendRenderType.Line),

    Scatter(LegendRenderType.Scatter);

    private final LegendRenderType legendRenderType;

    XYSeriesRenderStyle(LegendRenderType legendRenderType) {
        this.legendRenderType = legendRenderType;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return legendRenderType;
    }
}
