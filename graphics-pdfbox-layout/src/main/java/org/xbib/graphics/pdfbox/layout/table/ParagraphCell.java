package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.ParagraphCellRenderer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

public class ParagraphCell extends AbstractCell {

    protected float lineSpacing = 1f;

    private Paragraph paragraph;

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }

    public Paragraph getParagraph() {
        return paragraph;
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        while (getParagraph().getWrappedParagraph().removeLast() != null) {
        }
        for (ParagraphProcessor p : getParagraph().getProcessables()) {
            try {
                p.process(getParagraph().getWrappedParagraph(), getSettings());
            } catch (IOException exception) {
                throw new UncheckedIOException(exception);
            }
        }
        org.xbib.graphics.pdfbox.layout.elements.Paragraph wrappedParagraph = paragraph.getWrappedParagraph();
        wrappedParagraph.setLineSpacing(getLineSpacing());
        wrappedParagraph.setApplyLineSpacingToFirstLine(false);
        wrappedParagraph.setMaxWidth(width - getHorizontalPadding());
    }

    @Override
    protected Renderer createDefaultDrawer() {
        return new ParagraphCellRenderer(this);
    }

    @Override
    public float getMinHeight() {
        float height = paragraph.getWrappedParagraph().getHeight() + getVerticalPadding();
        return Math.max(height, super.getMinHeight());
    }

    public static class Paragraph {

        private final List<ParagraphProcessor> processables;

        private final org.xbib.graphics.pdfbox.layout.elements.Paragraph wrappedParagraph =
                new org.xbib.graphics.pdfbox.layout.elements.Paragraph();

        public Paragraph(List<ParagraphProcessor> processables) {
            this.processables = processables;
        }

        public List<ParagraphProcessor> getProcessables() {
            return processables;
        }

        public org.xbib.graphics.pdfbox.layout.elements.Paragraph getWrappedParagraph() {
            return wrappedParagraph;
        }

        /*public static class ParagraphBuilder {

            private final List<ParagraphProcessable> processables = new LinkedList<>();

            private ParagraphBuilder() {
            }

            public ParagraphBuilder append(StyledText styledText) {
                processables.add(styledText);
                return this;
            }

            public ParagraphBuilder append(Hyperlink hyperlink) {
                processables.add(hyperlink);
                return this;
            }

            public ParagraphBuilder append(Markup markup) {
                processables.add(markup);
                return this;
            }

            public ParagraphBuilder appendNewLine(Font font, float fontSize) {
                processables.add(new NewLine(font, fontSize));
                return this;
            }

            public Paragraph build() {
                return new Paragraph(processables);
            }
        }

        public static ParagraphBuilder builder() {
            return new ParagraphBuilder();
        }*/
    }

    /*public abstract static class ParagraphCellBuilder<C extends ParagraphCell, B extends ParagraphCell.ParagraphCellBuilder<C, B>> extends AbstractCellBuilder<C, B> {

        public B font(final Font font) {
            settings.setFont(font);
            return (B) this;
        }

        public B fontSize(final Integer fontSize) {
            settings.setFontSize(fontSize);
            return (B) this;
        }

        public B textColor(final Color textColor) {
            settings.setTextColor(textColor);
            return (B) this;
        }
    }*/
}
