package org.xbib.graphics.io.vector.pdf;

import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.Processor;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.filters.AbsoluteToRelativeTransformsFilter;
import org.xbib.graphics.io.vector.filters.FillPaintedShapeAsImageFilter;
import org.xbib.graphics.io.vector.filters.StateChangeGroupingFilter;
import org.xbib.graphics.io.vector.PageSize;
import java.io.IOException;

public class PDFProcessor implements Processor {

    private final boolean compressed;

    public PDFProcessor() {
        this(false);
    }

    public PDFProcessor(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public ProcessorResult process(Iterable<Command<?>> commands, PageSize pageSize) throws IOException  {
        AbsoluteToRelativeTransformsFilter absoluteToRelativeTransformsFilter = new AbsoluteToRelativeTransformsFilter(commands);
        FillPaintedShapeAsImageFilter paintedShapeAsImageFilter = new FillPaintedShapeAsImageFilter(absoluteToRelativeTransformsFilter);
        Iterable<Command<?>> filtered = new StateChangeGroupingFilter(paintedShapeAsImageFilter);
        PDFProcessorResult processorResult = new PDFProcessorResult(pageSize);
        processorResult.setCompressed(compressed);
        for (Command<?> command : filtered) {
            processorResult.handle(command);
        }
        processorResult.close();
        return processorResult;
    }
}
