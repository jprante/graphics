package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.font.Font;
import java.awt.Color;

public class Parameters {

    private Font font;

    private Float fontSize;

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

    public void fillingMergeBy(Parameters parameters) {
        fillingMergeFontSettings(parameters);
        fillingMergePaddingSettings(parameters);
        fillingMergeBorderWidthSettings(parameters);
        fillingMergeBorderStyleSettings(parameters);
        fillingMergeColorSettings(parameters);
        fillingMergeAlignmentSettings(parameters);
        fillingMergeWordBreakSetting(parameters);
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

    public Float getFontSize() {
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

    public void setFontSize(Float fontSize) {
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

    private void fillingMergeWordBreakSetting(Parameters parameters) {
        // Note that we use the boxed Boolean only here internally!
        if (wordBreak == null && parameters.wordBreak != null) {
            wordBreak = parameters.getWordBreak();
        }
    }

    private void fillingMergePaddingSettings(Parameters parameters) {
        if (getPaddingBottom() == null && parameters.getPaddingBottom() != null) {
            paddingBottom = parameters.getPaddingBottom();
        }

        if (getPaddingTop() == null && parameters.getPaddingTop() != null) {
            paddingTop = parameters.getPaddingTop();
        }

        if (getPaddingLeft() == null && parameters.getPaddingLeft() != null) {
            paddingLeft = parameters.getPaddingLeft();
        }

        if (getPaddingRight() == null && parameters.getPaddingRight() != null) {
            paddingRight = parameters.getPaddingRight();
        }
    }

    private void fillingMergeBorderWidthSettings(Parameters parameters) {
        if (getBorderWidthBottom() == null && parameters.getBorderWidthBottom() != null) {
            borderWidthBottom = parameters.getBorderWidthBottom();
        }

        if (getBorderWidthTop() == null && parameters.getBorderWidthTop() != null) {
            borderWidthTop = parameters.getBorderWidthTop();
        }

        if (getBorderWidthLeft() == null && parameters.getBorderWidthLeft() != null) {
            borderWidthLeft = parameters.getBorderWidthLeft();
        }

        if (getBorderWidthRight() == null && parameters.getBorderWidthRight() != null) {
            borderWidthRight = parameters.getBorderWidthRight();
        }
    }

    private void fillingMergeBorderStyleSettings(Parameters parameters) {
        if (getBorderStyleBottom() == null && parameters.getBorderStyleBottom() != null) {
            borderStyleBottom = parameters.getBorderStyleBottom();
        }

        if (getBorderStyleTop() == null && parameters.getBorderStyleTop() != null) {
            borderStyleTop = parameters.getBorderStyleTop();
        }

        if (getBorderStyleLeft() == null && parameters.getBorderStyleLeft() != null) {
            borderStyleLeft = parameters.getBorderStyleLeft();
        }

        if (getBorderStyleRight() == null && parameters.getBorderStyleRight() != null) {
            borderStyleRight = parameters.getBorderStyleRight();
        }
    }

    private void fillingMergeColorSettings(Parameters parameters) {
        if (getTextColor() == null && parameters.getTextColor() != null) {
            textColor = parameters.getTextColor();
        }

        if (getBackgroundColor() == null && parameters.getBackgroundColor() != null) {
            backgroundColor = parameters.getBackgroundColor();
        }

        if (getBorderColor() == null && parameters.getBorderColor() != null) {
            borderColor = parameters.getBorderColor();
        }
    }

    private void fillingMergeAlignmentSettings(Parameters parameters) {
        if (getHorizontalAlignment() == null && parameters.getHorizontalAlignment() != null) {
            horizontalAlignment = parameters.getHorizontalAlignment();
        }

        if (getVerticalAlignment() == null && parameters.getVerticalAlignment() != null) {
            verticalAlignment = parameters.getVerticalAlignment();
        }
    }

    private void fillingMergeFontSettings(Parameters parameters) {
        if (getFont() == null && parameters.getFont() != null) {
            font = parameters.getFont();
        }
        if (getFontSize() == null && parameters.getFontSize() != null) {
            fontSize = parameters.getFontSize();
        }
    }
}
