package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.TextCellRenderer;
import java.awt.Color;

public class TextCell extends AbstractTextCell {

    protected String text;

    protected Renderer createDefaultDrawer() {
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
        
        private final Settings settings;
        
        private String text;

        private int colSpan;

        private int rowSpan;

        private Builder() {
            settings = new Settings();
        }
        
        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public Builder font(Font font) {
            settings.setFont(font);
            return this;
        }

        public Builder fontSize(Integer fontSize) {
            settings.setFontSize(fontSize);
            return this;
        }

        public Builder textColor(Color textColor) {
            settings.setTextColor(textColor);
            return this;
        }

        public Builder borderWidth(float borderWidth) {
            settings.setBorderWidthTop(borderWidth);
            settings.setBorderWidthBottom(borderWidth);
            settings.setBorderWidthLeft(borderWidth);
            settings.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderWidthTop(final float borderWidth) {
            settings.setBorderWidthTop(borderWidth);
            return this;
        }

        public Builder borderWidthBottom(final float borderWidth) {
            settings.setBorderWidthBottom(borderWidth);
            return this;
        }

        public Builder borderWidthLeft(final float borderWidth) {
            settings.setBorderWidthLeft(borderWidth);
            return this;
        }

        public Builder borderWidthRight(final float borderWidth) {
            settings.setBorderWidthRight(borderWidth);
            return this;
        }

        public Builder borderStyleTop(final BorderStyleInterface style) {
            settings.setBorderStyleTop(style);
            return this;
        }

        public Builder borderStyleBottom(final BorderStyleInterface style) {
            settings.setBorderStyleBottom(style);
            return this;
        }

        public Builder borderStyleLeft(final BorderStyleInterface style) {
            settings.setBorderStyleLeft(style);
            return this;
        }

        public Builder borderStyleRight(final BorderStyleInterface style) {
            settings.setBorderStyleRight(style);
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
            settings.setPaddingTop(padding);
            return this;
        }

        public Builder paddingBottom(final float padding) {
            settings.setPaddingBottom(padding);
            return this;
        }

        public Builder paddingLeft(final float padding) {
            settings.setPaddingLeft(padding);
            return this;
        }

        public Builder paddingRight(final float padding) {
            settings.setPaddingRight(padding);
            return this;
        }

        public Builder horizontalAlignment(final HorizontalAlignment alignment) {
            settings.setHorizontalAlignment(alignment);
            return this;
        }

        public Builder verticalAlignment(final VerticalAlignment alignment) {
            settings.setVerticalAlignment(alignment);
            return this;
        }

        public Builder backgroundColor(final Color backgroundColor) {
            settings.setBackgroundColor(backgroundColor);
            return this;
        }

        public Builder borderColor(final Color borderColor) {
            settings.setBorderColor(borderColor);
            return this;
        }

        public Builder wordBreak(final Boolean wordBreak) {
            settings.setWordBreak(wordBreak);
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
            cell.setSettings(settings);
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
