package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Paragraph;
import java.io.IOException;

public interface ParagraphProcessor {

    void process(Paragraph paragraph, Parameters parameters) throws IOException;

}
