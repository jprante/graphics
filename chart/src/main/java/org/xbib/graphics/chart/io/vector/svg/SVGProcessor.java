package org.xbib.graphics.chart.io.vector.svg;

import org.xbib.graphics.chart.io.vector.Document;
import org.xbib.graphics.chart.io.vector.Processor;
import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.filters.FillPaintedShapeAsImageFilter;
import org.xbib.graphics.chart.io.vector.intermediate.filters.StateChangeGroupingFilter;
import org.xbib.graphics.chart.io.vector.util.PageSize;

public class SVGProcessor implements Processor {
    public Document process(Iterable<Command<?>> commands, PageSize pageSize) {
        FillPaintedShapeAsImageFilter shapesAsImages = new FillPaintedShapeAsImageFilter(commands);
        Iterable<Command<?>> filtered = new StateChangeGroupingFilter(shapesAsImages);
        SVGDocument doc = new SVGDocument(pageSize);
        for (Command<?> command : filtered) {
            doc.handle(command);
        }
        doc.close();
        return doc;
    }
}
