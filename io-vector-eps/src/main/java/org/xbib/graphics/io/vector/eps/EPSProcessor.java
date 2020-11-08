package org.xbib.graphics.io.vector.eps;

import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.PageSize;
import org.xbib.graphics.io.vector.Processor;
import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.filters.FillPaintedShapeAsImageFilter;
import java.io.IOException;

public class EPSProcessor implements Processor {

    @Override
    public ProcessorResult process(Iterable<Command<?>> commands, PageSize pageSize) throws IOException {
        FillPaintedShapeAsImageFilter paintedShapeAsImageFilter = new FillPaintedShapeAsImageFilter(commands);
        EPSProcessorResult epsDocument = new EPSProcessorResult(pageSize);
        for (Command<?> command : paintedShapeAsImageFilter) {
            epsDocument.handle(command);
        }
        epsDocument.close();
        return epsDocument;
    }
}
