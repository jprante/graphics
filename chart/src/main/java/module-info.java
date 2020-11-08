module org.xbib.graphics.chart {
    exports org.xbib.graphics.chart;
    exports org.xbib.graphics.chart.axis;
    exports org.xbib.graphics.chart.bubble;
    exports org.xbib.graphics.chart.category;
    exports org.xbib.graphics.chart.formatter;
    exports org.xbib.graphics.chart.io;
    exports org.xbib.graphics.chart.legend;
    exports org.xbib.graphics.chart.ohlc;
    exports org.xbib.graphics.chart.pie;
    exports org.xbib.graphics.chart.plot;
    exports org.xbib.graphics.chart.series;
    exports org.xbib.graphics.chart.style;
    exports org.xbib.graphics.chart.theme;
    exports org.xbib.graphics.chart.xy;
    requires org.xbib.graphics.io.vector;
    requires org.xbib.graphics.io.vector.eps;
    requires org.xbib.graphics.io.vector.pdf;
    requires org.xbib.graphics.io.vector.svg;
    requires transitive java.desktop;
}
