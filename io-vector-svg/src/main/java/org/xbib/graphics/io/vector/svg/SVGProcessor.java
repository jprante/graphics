package org.xbib.graphics.io.vector.svg;

import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.Processor;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.filters.FillPaintedShapeAsImageFilter;
import org.xbib.graphics.io.vector.filters.StateChangeGroupingFilter;
import org.xbib.graphics.io.vector.PageSize;

public class SVGProcessor implements Processor {
    public ProcessorResult process(Iterable<Command<?>> commands, PageSize pageSize) {
        FillPaintedShapeAsImageFilter shapesAsImages = new FillPaintedShapeAsImageFilter(commands);
        Iterable<Command<?>> filtered = new StateChangeGroupingFilter(shapesAsImages);
        SVGProcessorResult doc = new SVGProcessorResult(pageSize);
        for (Command<?> command : filtered) {
            doc.handle(command);
        }
        doc.close();
        return doc;
    }
}
