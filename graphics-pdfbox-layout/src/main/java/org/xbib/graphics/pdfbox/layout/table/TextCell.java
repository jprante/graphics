package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.TextCellRenderer;
import java.awt.Color;

public class TextCell extends AbstractTextCell {

    protected String text;

    @Override
    protected Renderer createDefaultRenderer() {
        return new TextCellRenderer<>(this);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        
        private final Parameters parameters;
        
        private String text;

        private int colSpan;

        private int rowSpan;

        private Builder() {
            parameters = new Parameters();
        }
        
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder font(Font font) {
            parameters.setFont(font);
            return this;
        }

        public Builder fontSize(Float fontSize) {
            parameters.setFontSize(fontSize);
            return this;
        }

        public Builder textColor(Color textColor) {
            parameters.setTextColor(textColor);
            return this;
        }

        public Builder borderWidth(float borderWidth) {
            parameters.setBorderWidthTop(borderWidth);
            parameters.setBorderWidthBottom(borderWidth);
            parameters.setBorderWidthLeft(borderWidth);
            parameters.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderWidthTop(final float borderWidth) {
            parameters.setBorderWidthTop(borderWidth);
            return this;
        }

        public Builder borderWidthBottom(final float borderWidth) {
            parameters.setBorderWidthBottom(borderWidth);
            return this;
        }

        public Builder borderWidthLeft(final float borderWidth) {
            parameters.setBorderWidthLeft(borderWidth);
            return this;
        }

        public Builder borderWidthRight(final float borderWidth) {
            parameters.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderStyleTop(final BorderStyleInterface style) {
            parameters.setBorderStyleTop(style);
            return this;
        }

        public Builder borderStyleBottom(final BorderStyleInterface style) {
            parameters.setBorderStyleBottom(style);
            return this;
        }

        public Builder borderStyleLeft(final BorderStyleInterface style) {
            parameters.setBorderStyleLeft(style);
            return this;
        }

        public Builder borderStyleRight(final BorderStyleInterface style) {
            parameters.setBorderStyleRight(style);
            return this;
        }

        public Builder borderStyle(final BorderStyleInterface style) {
            return this.borderStyleLeft(style)
                    .borderStyleRight(style)
                    .borderStyleBottom(style)
                    .borderStyleTop(style);
        }

        public Builder padding(final float padding) {
            return this.paddingTop(padding)
                    .paddingBottom(padding)
                    .paddingLeft(padding)
                    .paddingRight(padding);
        }

        public Builder paddingTop(final float padding) {
            parameters.setPaddingTop(padding);
            return this;
        }

        public Builder paddingBottom(final float padding) {
            parameters.setPaddingBottom(padding);
            return this;
        }

        public Builder paddingLeft(final float padding) {
            parameters.setPaddingLeft(padding);
            return this;
        }

        public Builder paddingRight(final float padding) {
            parameters.setPaddingRight(padding);
            return this;
        }

        public Builder horizontalAlignment(final HorizontalAlignment alignment) {
            parameters.setHorizontalAlignment(alignment);
            return this;
        }

        public Builder verticalAlignment(final VerticalAlignment alignment) {
            parameters.setVerticalAlignment(alignment);
            return this;
        }

        public Builder backgroundColor(final Color backgroundColor) {
            parameters.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder borderColor(final Color borderColor) {
            parameters.setBorderColor(borderColor);
            return this;
        }

        public Builder wordBreak(final Boolean wordBreak) {
            parameters.setWordBreak(wordBreak);
            return this;
        }

        public Builder colSpan(int colSpan) {
            this.colSpan = colSpan;
            return this;
        }

        public Builder rowSpan(int rowSpan) {
            this.rowSpan = rowSpan;
            return this;
        }

        public TextCell build() {
            TextCell cell = new TextCell();
            cell.setParameters(parameters);
            cell.setText(text);
            if (colSpan > 0) {
                cell.setColSpan(colSpan);
            }
            if (rowSpan > 0) {
                cell.setRowSpan(rowSpan);
            }
            return cell;
        }
    }
}
