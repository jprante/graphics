package org.xbib.graphics.pdfbox.layout.elements;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.xbib.graphics.pdfbox.layout.elements.render.Layout;
import org.xbib.graphics.pdfbox.layout.elements.render.LayoutHint;
import org.xbib.graphics.pdfbox.layout.elements.render.RenderContext;
import org.xbib.graphics.pdfbox.layout.elements.render.RenderListener;
import org.xbib.graphics.pdfbox.layout.elements.render.Renderer;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayout;
import org.xbib.graphics.pdfbox.layout.elements.render.VerticalLayoutHint;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The central class for creating a document.
 */
public class Document implements Element, Closeable, RenderListener {

    private final List<Entry<Element, LayoutHint>> elements = new ArrayList<>();

    private final List<Renderer> customRenderer = new ArrayList<>();

    private final List<RenderListener> renderListener = new ArrayList<>();

    private PageFormat pageFormat;

    private final PDDocument pdDocument;

    private final PDDocumentInformation pdDocumentInformation;

    /**
     * Creates a Document.
     */
    public Document() {
        this(PageFormats.A4_PORTRAIT, true);
    }

    /**
     * Creates a Document based on the given page format. By default, a
     * {@link VerticalLayout} is used.
     *
     * @param pageFormat the page format box to use.
     */
    public Document(PageFormat pageFormat) {
        this(pageFormat, true);
    }

    /**
     * Creates a Document in A4 with orientation portrait and the given margins.
     * By default, a {@link VerticalLayout} is used.
     *
     * @param marginLeft   the left margin
     * @param marginRight  the right margin
     * @param marginTop    the top margin
     * @param marginBottom the bottom margin
     */
    public Document(float marginLeft,
                    float marginRight,
                    float marginTop,
                    float marginBottom) {
        this(marginLeft, marginRight, marginTop, marginBottom, true);
    }

    public Document(float marginLeft,
                    float marginRight,
                    float marginTop,
                    float marginBottom, boolean memory) {
        this(PageFormat.builder().margins(marginLeft, marginRight, marginTop, marginBottom).build(), memory);
    }

    public Document(PageFormat pageFormat, boolean memory) {
        this.pdDocument = new PDDocument(memory ? MemoryUsageSetting.setupMainMemoryOnly() : MemoryUsageSetting.setupTempFileOnly());
        this.pdDocumentInformation = new PDDocumentInformation();
        setPageFormat(pageFormat);
    }

    public void setPageFormat(PageFormat pageFormat) {
        this.pageFormat = pageFormat;
    }

    public PDDocument getPdDocument() {
        return pdDocument;
    }

    public void setAuthor(String author) {
        pdDocumentInformation.setAuthor(author);
    }

    public void setCreator(String creator) {
        pdDocumentInformation.setCreator(creator);
    }

    public void setTitle(String title) {
        pdDocumentInformation.setTitle(title);
    }

    public void setSubject(String subject) {
        pdDocumentInformation.setSubject(subject);
    }

    public void setKeywords(String keywords) {
        pdDocumentInformation.setKeywords(keywords);
    }

    public void setProducer(String producer) {
        pdDocumentInformation.setProducer(producer);
    }

    public void setTrapped(String value) {
        pdDocumentInformation.setTrapped(value);
    }

    public void setCreationDate(Instant instant) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Calendar calendar = GregorianCalendar.from(zdt);
        pdDocumentInformation.setCreationDate(calendar);
    }

    public void setModificationDate(Instant instant) {
        ZonedDateTime zdt = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
        Calendar calendar = GregorianCalendar.from(zdt);
        pdDocumentInformation.setModificationDate(calendar);
    }

    public void setCustomMetadata(String key, String value) {
        pdDocumentInformation.setCustomMetadataValue(key, value);
    }

    /**
     * Adds an element to the document using a {@link VerticalLayoutHint}.
     *
     * @param element the element to add
     */
    @Override
    public Element add(Element element) {
        add(element, new VerticalLayoutHint());
        return this;
    }

    /**
     * Adds an element with the given layout hint.
     *
     * @param element    the element to add
     * @param layoutHint the hint for the {@link Layout}.
     */
    public Element add(Element element, LayoutHint layoutHint) {
        elements.add(Map.entry(element, layoutHint));
        return this;
    }

    /**
     * @return the page format to use as default.
     */
    public PageFormat getPageFormat() {
        return pageFormat;
    }

    /**
     * @return the media box width minus margins.
     */
    public float getPageWidth() {
        return pageFormat.getMediaBox().getWidth() -
                pageFormat.getMarginLeft() - pageFormat.getMarginRight();
    }

    /**
     * @return the media box height minus margins.
     */
    public float getPageHeight() {
        return pageFormat.getMediaBox().getHeight() -
                pageFormat.getMarginTop() - pageFormat.getMarginBottom();
    }

    /**
     * Adds a (custom) {@link Renderer} that may handle the rendering of an
     * element. All renderers will be asked to render the current element in the
     * order they have been added. If no renderer is capable, the default
     * renderer will be asked.
     *
     * @param renderer the renderer to add.
     */
    public void addRenderer(final Renderer renderer) {
        if (renderer != null) {
            customRenderer.add(renderer);
        }
    }

    /**
     * Adds a {@link RenderListener} that will be notified during rendering.
     *
     * @param listener the listener to add.
     */
    public void addRenderListener(final RenderListener listener) {
        if (listener != null) {
            renderListener.add(listener);
        }
    }

    @Override
    public void beforePage(RenderContext renderContext) {
        for (RenderListener listener : renderListener) {
            listener.beforePage(renderContext);
        }
    }

    @Override
    public void afterPage(RenderContext renderContext) {
        for (RenderListener listener : renderListener) {
            listener.afterPage(renderContext);
        }
    }

    /**
     * Renders all elements and returns the resulting {@link PDDocument}.
     *
     * @return the resulting {@link PDDocument}
     * @throws IOException by pdfbox
     */
    public Document render() throws IOException {
        pdDocument.setDocumentInformation(pdDocumentInformation);
        RenderContext renderContext = new RenderContext(this, pdDocument);
        for (Entry<Element, LayoutHint> entry : elements) {
            Element element = entry.getKey();
            LayoutHint layoutHint = entry.getValue();
            boolean success = false;
            Iterator<Renderer> customRendererIterator = customRenderer.iterator();
            while (!success && customRendererIterator.hasNext()) {
                success = customRendererIterator.next().render(renderContext, element, layoutHint);
            }
            if (!success) {
                success = renderContext.render(renderContext, element, layoutHint);
            }
            if (!success) {
                throw new IllegalArgumentException(String.format(
                                "neither layout %s nor the render context knows what to do with %s",
                                renderContext.getLayout(), element));
            }
        }
        renderContext.close();
        return this;
    }

    public synchronized Document save(OutputStream outputStream) throws IOException {
        if (pdDocument != null && outputStream != null) {
            pdDocument.save(outputStream);
            outputStream.close();
        }
        return this;
    }

    @Override
    public void close() throws IOException {
        if (pdDocument != null) {
            pdDocument.close();
        }
    }
}
