package org.xbib.graphics.pdfbox.layout.table;

import org.xbib.graphics.pdfbox.layout.element.Element;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;

import java.awt.Color;

public interface Cell extends Element {

    int getRowSpan();

    int getColSpan();

    void setRow(Row row);

    Row getRow();

    void setColumn(Column column);

    Column getColumn();

    void setWidth(float width);

    Parameters getParameters();

    float getMinHeight();

    float getWidth();

    float getHeight();

    Renderer getRenderer();

    boolean hasBackgroundColor();

    Color getBackgroundColor();

    Color getBorderColor();

    float getPaddingLeft();

    float getPaddingRight();

    float getPaddingTop();

    float getPaddingBottom();

    boolean hasBorderLeft();

    boolean hasBorderRight();

    boolean hasBorderTop();

    boolean hasBorderBottom();

    float getBorderWidthLeft();

    float getBorderWidthRight();

    float getBorderWidthTop();

    float getBorderWidthBottom();

    BorderStyleInterface getBorderStyleLeft();

    BorderStyleInterface getBorderStyleRight();

    BorderStyleInterface getBorderStyleTop();

    BorderStyleInterface getBorderStyleBottom();

    float calculateHeightForRowSpan();

    boolean isHorizontallyAligned(HorizontalAlignment alignment);

    boolean isVerticallyAligned(VerticalAlignment alignment);

}
