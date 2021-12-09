package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.elements.Paragraph;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.ParagraphCellRenderer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class ParagraphCell extends AbstractCell {

    protected float lineSpacing = 1f;

    private CellParagraph cellParagraph;

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setCellParagraph(CellParagraph cellParagraph) {
        this.cellParagraph = cellParagraph;
    }

    public CellParagraph getCellParagraph() {
        return cellParagraph;
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        //while (getCellParagraph().getParagraph().removeLast() != null) {
        //}
        for (ParagraphProcessor p : getCellParagraph().getParagraphProcessors()) {
            try {
                p.process(getCellParagraph().getParagraph(), getParameters());
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
        Paragraph paragraph = cellParagraph.getParagraph();
        paragraph.setLineSpacing(getLineSpacing());
        paragraph.setApplyLineSpacingToFirstLine(false);
        paragraph.setMaxWidth(width - getHorizontalPadding());
    }

    @Override
    protected Renderer createDefaultRenderer() {
        return new ParagraphCellRenderer(this);
    }

    @Override
    public float getMinHeight() {
        float height = cellParagraph.getParagraph().getHeight() + getVerticalPadding();
        return Math.max(height, super.getMinHeight());
    }

    public static class CellParagraph {

        private final Paragraph paragraph;

        private final List<ParagraphProcessor> paragraphProcessors;

        public CellParagraph(Paragraph paragraph, List<ParagraphProcessor> processables) {
            this.paragraph = paragraph;
            this.paragraphProcessors = processables;
        }

        public List<ParagraphProcessor> getParagraphProcessors() {
            return paragraphProcessors;
        }

        public Paragraph getParagraph() {
            return paragraph;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final Parameters parameters;

        private int colSpan;

        private int rowSpan;

        private Paragraph paragraph;

        private final List<ParagraphProcessor> processors;

        private Builder() {
            this.parameters = new Parameters();
            this.processors = new ArrayList<>();
        }

        public Builder colSpan(int colSpan) {
            this.colSpan = colSpan;
            return this;
        }

        public Builder rowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
            return this;
        }

        public Builder paragraph(Paragraph paragraph) {
            this.paragraph = paragraph;
            return this;
        }

        public Builder add(ParagraphProcessor processor) {
            this.processors.add(processor);
            return this;
        }

        public ParagraphCell build() {
            ParagraphCell cell = new ParagraphCell();
            cell.setParameters(parameters);
            cell.setColSpan(colSpan);
            cell.setRowSpan(rowSpan);
            cell.setCellParagraph(new CellParagraph(paragraph, processors));
            return cell;
        }
    }
}
