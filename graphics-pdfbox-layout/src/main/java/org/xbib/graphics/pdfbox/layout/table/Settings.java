package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;
import java.awt.Color;

public class Settings {

    private Font font;

    private Integer fontSize;

    private Color textColor;

    private Color backgroundColor;

    private Float borderWidthTop;

    private Float borderWidthBottom;

    private Float borderWidthLeft;

    private Float borderWidthRight;

    private Color borderColor;

    private Float paddingLeft;

    private Float paddingRight;

    private Float paddingTop;

    private Float paddingBottom;

    private BorderStyleInterface borderStyleLeft;

    private BorderStyleInterface borderStyleRight;

    private BorderStyleInterface borderStyleTop;

    private BorderStyleInterface borderStyleBottom;

    private HorizontalAlignment horizontalAlignment;

    private VerticalAlignment verticalAlignment;

    // We use a boxed Boolean internally in order to be able to model the absence of a value.
    // For callers outside it should expose only the primitive though.
    private Boolean wordBreak;

    public boolean isWordBreak() {
        return wordBreak != null && wordBreak;
    }

    public void setWordBreak(boolean wordBreak) {
        this.wordBreak = wordBreak;
    }

    public void fillingMergeBy(Settings settings) {
        fillingMergeFontSettings(settings);
        fillingMergePaddingSettings(settings);
        fillingMergeBorderWidthSettings(settings);
        fillingMergeBorderStyleSettings(settings);
        fillingMergeColorSettings(settings);
        fillingMergeAlignmentSettings(settings);
        fillingMergeWordBreakSetting(settings);
    }

    public Boolean getWordBreak() {
        return wordBreak;
    }

    public BorderStyleInterface getBorderStyleBottom() {
        return borderStyleBottom;
    }

    public BorderStyleInterface getBorderStyleLeft() {
        return borderStyleLeft;
    }

    public BorderStyleInterface getBorderStyleRight() {
        return borderStyleRight;
    }

    public BorderStyleInterface getBorderStyleTop() {
        return borderStyleTop;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Float getBorderWidthBottom() {
        return borderWidthBottom;
    }

    public Float getBorderWidthLeft() {
        return borderWidthLeft;
    }

    public Float getBorderWidthRight() {
        return borderWidthRight;
    }

    public Float getBorderWidthTop() {
        return borderWidthTop;
    }

    public Float getPaddingBottom() {
        return paddingBottom;
    }

    public Float getPaddingLeft() {
        return paddingLeft;
    }

    public Float getPaddingRight() {
        return paddingRight;
    }

    public Float getPaddingTop() {
        return paddingTop;
    }

    public HorizontalAlignment getHorizontalAlignment() {
        return horizontalAlignment;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public Font getFont() {
        return font;
    }

    public VerticalAlignment getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public void setBorderStyleBottom(BorderStyleInterface borderStyleBottom) {
        this.borderStyleBottom = borderStyleBottom;
    }

    public void setBorderStyleLeft(BorderStyleInterface borderStyleLeft) {
        this.borderStyleLeft = borderStyleLeft;
    }

    public void setBorderStyleRight(BorderStyleInterface borderStyleRight) {
        this.borderStyleRight = borderStyleRight;
    }

    public void setBorderStyleTop(BorderStyleInterface borderStyleTop) {
        this.borderStyleTop = borderStyleTop;
    }

    public void setBorderWidthBottom(Float borderWidthBottom) {
        this.borderWidthBottom = borderWidthBottom;
    }

    public void setBorderWidthLeft(Float borderWidthLeft) {
        this.borderWidthLeft = borderWidthLeft;
    }

    public void setBorderWidthRight(Float borderWidthRight) {
        this.borderWidthRight = borderWidthRight;
    }

    public void setBorderWidthTop(Float borderWidthTop) {
        this.borderWidthTop = borderWidthTop;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }

    public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public void setPaddingBottom(Float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPaddingLeft(Float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public void setPaddingRight(Float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public void setPaddingTop(Float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public void setWordBreak(Boolean wordBreak) {
        this.wordBreak = wordBreak;
    }

    private void fillingMergeWordBreakSetting(Settings settings) {
        // Note that we use the boxed Boolean only here internally!
        if (wordBreak == null && settings.wordBreak != null) {
            wordBreak = settings.getWordBreak();
        }
    }

    private void fillingMergePaddingSettings(Settings settings) {
        if (getPaddingBottom() == null && settings.getPaddingBottom() != null) {
            paddingBottom = settings.getPaddingBottom();
        }

        if (getPaddingTop() == null && settings.getPaddingTop() != null) {
            paddingTop = settings.getPaddingTop();
        }

        if (getPaddingLeft() == null && settings.getPaddingLeft() != null) {
            paddingLeft = settings.getPaddingLeft();
        }

        if (getPaddingRight() == null && settings.getPaddingRight() != null) {
            paddingRight = settings.getPaddingRight();
        }
    }

    private void fillingMergeBorderWidthSettings(Settings settings) {
        if (getBorderWidthBottom() == null && settings.getBorderWidthBottom() != null) {
            borderWidthBottom = settings.getBorderWidthBottom();
        }

        if (getBorderWidthTop() == null && settings.getBorderWidthTop() != null) {
            borderWidthTop = settings.getBorderWidthTop();
        }

        if (getBorderWidthLeft() == null && settings.getBorderWidthLeft() != null) {
            borderWidthLeft = settings.getBorderWidthLeft();
        }

        if (getBorderWidthRight() == null && settings.getBorderWidthRight() != null) {
            borderWidthRight = settings.getBorderWidthRight();
        }
    }

    private void fillingMergeBorderStyleSettings(Settings settings) {
        if (getBorderStyleBottom() == null && settings.getBorderStyleBottom() != null) {
            borderStyleBottom = settings.getBorderStyleBottom();
        }

        if (getBorderStyleTop() == null && settings.getBorderStyleTop() != null) {
            borderStyleTop = settings.getBorderStyleTop();
        }

        if (getBorderStyleLeft() == null && settings.getBorderStyleLeft() != null) {
            borderStyleLeft = settings.getBorderStyleLeft();
        }

        if (getBorderStyleRight() == null && settings.getBorderStyleRight() != null) {
            borderStyleRight = settings.getBorderStyleRight();
        }
    }

    private void fillingMergeColorSettings(Settings settings) {
        if (getTextColor() == null && settings.getTextColor() != null) {
            textColor = settings.getTextColor();
        }

        if (getBackgroundColor() == null && settings.getBackgroundColor() != null) {
            backgroundColor = settings.getBackgroundColor();
        }

        if (getBorderColor() == null && settings.getBorderColor() != null) {
            borderColor = settings.getBorderColor();
        }
    }

    private void fillingMergeAlignmentSettings(Settings settings) {
        if (getHorizontalAlignment() == null && settings.getHorizontalAlignment() != null) {
            horizontalAlignment = settings.getHorizontalAlignment();
        }

        if (getVerticalAlignment() == null && settings.getVerticalAlignment() != null) {
            verticalAlignment = settings.getVerticalAlignment();
        }
    }

    private void fillingMergeFontSettings(Settings settings) {
        if (getFont() == null && settings.getFont() != null) {
            font = settings.getFont();
        }
        if (getFontSize() == null && settings.getFontSize() != null) {
            fontSize = settings.getFontSize();
        }
    }
}
