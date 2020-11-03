package org.xbib.graphics.chart.bubble;

import org.xbib.graphics.chart.legend.LegendRenderable;
import org.xbib.graphics.chart.legend.LegendRenderType;

public enum BubbleSeriesRenderStyle implements LegendRenderable {
    Round(LegendRenderType.Box);

    private final LegendRenderType legendRenderType;

    BubbleSeriesRenderStyle(LegendRenderType legendRenderType) {
        this.legendRenderType = legendRenderType;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return legendRenderType;
    }
}
