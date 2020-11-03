package org.xbib.graphics.chart.io.vector.eps;

import org.xbib.graphics.chart.io.vector.Document;
import org.xbib.graphics.chart.io.vector.Processor;
import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.filters.FillPaintedShapeAsImageFilter;
import org.xbib.graphics.chart.io.vector.util.PageSize;

public class EPSProcessor implements Processor {
    public Document process(Iterable<Command<?>> commands, PageSize pageSize) {
        // TODO Apply rotate(theta,x,y) => translate-rotate-translate filter
        // TODO Apply image transparency => image mask filter
        // TODO Apply optimization filter
        FillPaintedShapeAsImageFilter paintedShapeAsImageFilter = new FillPaintedShapeAsImageFilter(commands);
        EPSDocument doc = new EPSDocument(pageSize);
        for (Command<?> command : paintedShapeAsImageFilter) {
            doc.handle(command);
        }
        doc.close();
        return doc;
    }
}

