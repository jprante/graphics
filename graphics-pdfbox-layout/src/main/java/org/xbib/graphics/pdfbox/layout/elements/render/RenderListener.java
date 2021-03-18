package org.xbib.graphics.pdfbox.layout.elements.render;

/**
 * A render listener is called before and after a page has been rendered. It may
 * be used, to perform some custom operations (drawings) to the page.
 */
public interface RenderListener {

    /**
     * Called before any rendering is performed to the page.
     *
     * @param renderContext the context providing all rendering state.
     */
    void beforePage(RenderContext renderContext);

    /**
     * Called after any rendering is performed to the page.
     *
     * @param renderContext the context providing all rendering state.
     */
    void afterPage(RenderContext renderContext);
}
