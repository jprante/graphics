package org.xbib.graphics.chart.io.vector.pdf;

import org.xbib.graphics.chart.io.vector.Document;
import org.xbib.graphics.chart.io.vector.Processor;
import org.xbib.graphics.chart.io.vector.intermediate.commands.Command;
import org.xbib.graphics.chart.io.vector.intermediate.filters.AbsoluteToRelativeTransformsFilter;
import org.xbib.graphics.chart.io.vector.intermediate.filters.FillPaintedShapeAsImageFilter;
import org.xbib.graphics.chart.io.vector.intermediate.filters.StateChangeGroupingFilter;
import org.xbib.graphics.chart.io.vector.util.PageSize;

public class PDFProcessor implements Processor {

    public Document process(Iterable<Command<?>> commands, PageSize pageSize) {
        AbsoluteToRelativeTransformsFilter absoluteToRelativeTransformsFilter = new AbsoluteToRelativeTransformsFilter(commands);
        FillPaintedShapeAsImageFilter paintedShapeAsImageFilter = new FillPaintedShapeAsImageFilter(absoluteToRelativeTransformsFilter);
        Iterable<Command<?>> filtered = new StateChangeGroupingFilter(paintedShapeAsImageFilter);
        PDFDocument doc = new PDFDocument(pageSize);
        for (Command<?> command : filtered) {
            doc.handle(command);
        }
        doc.close();
        return doc;
    }
}

