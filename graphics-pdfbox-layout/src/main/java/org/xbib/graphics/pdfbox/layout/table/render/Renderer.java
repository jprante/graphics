package org.xbib.graphics.pdfbox.layout.table.render;

public interface Renderer {

    void renderContent(RenderContext renderContext);

    void renderBackground(RenderContext renderContext);

    void renderBorders(RenderContext renderContext);

}
