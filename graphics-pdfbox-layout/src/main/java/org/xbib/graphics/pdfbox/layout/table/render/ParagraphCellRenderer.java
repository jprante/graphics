package org.xbib.graphics.pdfbox.layout.table.render;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.ParagraphCell;
import org.xbib.graphics.pdfbox.layout.text.Alignment;
import org.xbib.graphics.pdfbox.layout.text.DrawContext;
import org.xbib.graphics.pdfbox.layout.text.Position;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationDrawListener;

import java.util.EnumMap;
import java.util.Map;

public class ParagraphCellRenderer extends AbstractCellRenderer<ParagraphCell> {

    private static final Map<HorizontalAlignment, Alignment> ALIGNMENT_MAP = new EnumMap<>(HorizontalAlignment.class);
    static {
        ALIGNMENT_MAP.put(HorizontalAlignment.LEFT, Alignment.LEFT);
        ALIGNMENT_MAP.put(HorizontalAlignment.RIGHT, Alignment.RIGHT);
        ALIGNMENT_MAP.put(HorizontalAlignment.CENTER, Alignment.CENTER);
        ALIGNMENT_MAP.put(HorizontalAlignment.JUSTIFY, Alignment.JUSTIFY);
    }

    public ParagraphCellRenderer(ParagraphCell cell) {
        this.cell = cell;
    }

    @Override
    public void renderContent(RenderContext renderContext) {
        Paragraph paragraph = cell.getCellParagraph().getParagraph();
        float x = renderContext.getStartingPoint().x + cell.getPaddingLeft();
        float y = renderContext.getStartingPoint().y + getAdaptionForVerticalAlignment();
        AnnotationDrawListener annotationDrawListener = createAndGetAnnotationDrawListenerWith(renderContext);
        paragraph.drawText(renderContext.getContentStream(),
                new Position(x, y),
                ALIGNMENT_MAP.getOrDefault(cell.getParameters().getHorizontalAlignment(), Alignment.LEFT),
                annotationDrawListener
        );
    }

    @Override
    protected float calculateInnerHeight() {
        return cell.getCellParagraph().getParagraph().getHeight();
    }

    private AnnotationDrawListener createAndGetAnnotationDrawListenerWith(RenderContext renderContext) {
        return new AnnotationDrawListener(new DrawContext() {
                @Override
                public PDDocument getPdDocument() {
                    return renderContext.getPdDocument();
                }

                @Override
                public PDPage getCurrentPage() {
                    return renderContext.getPage();
                }

                @Override
                public PDPageContentStream getCurrentPageContentStream() {
                    return renderContext.getContentStream();
                }
            });
    }

}
