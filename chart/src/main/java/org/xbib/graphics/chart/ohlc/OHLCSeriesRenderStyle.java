package org.xbib.graphics.chart.ohlc;

import org.xbib.graphics.chart.legend.LegendRenderable;
import org.xbib.graphics.chart.legend.LegendRenderType;

public enum OHLCSeriesRenderStyle implements LegendRenderable {
    Candle(LegendRenderType.Line),

    HiLo(LegendRenderType.Line);

    private final LegendRenderType legendRenderType;

    OHLCSeriesRenderStyle(LegendRenderType legendRenderType) {
        this.legendRenderType = legendRenderType;
    }

    @Override
    public LegendRenderType getLegendRenderType() {
        return legendRenderType;
    }
}
