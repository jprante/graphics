package org.xbib.graphics.pdfbox.layout.table;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.xbib.graphics.pdfbox.layout.table.render.Renderer;
import org.xbib.graphics.pdfbox.layout.table.render.RenderContext;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

public class TableRenderer {

    protected final Table table;

    protected PDDocument pdDocument;

    protected PDPageContentStream contentStream;

    protected PDPage page;

    protected float startX;

    protected float startY;

    protected float endY;

    protected boolean compress;

    protected PDPage tableStartPage;

    protected boolean startTableInNewPage;

    protected final List<BiConsumer<Renderer, RenderContext>> drawerList = new LinkedList<>();

    {
        this.drawerList.add((drawer, drawingContext) -> {
            drawer.renderBackground(drawingContext);
            drawer.renderContent(drawingContext);
        });
        this.drawerList.add(Renderer::renderBorders);
    }

    public TableRenderer(Table table) {
        this.table = table;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public void setDocument(PDDocument pdDocument) {
        this.pdDocument = pdDocument;
    }

    public void setContentStream(PDPageContentStream contentStream) {
        this.contentStream = contentStream;
    }

    public void setPage(PDPage page) {
        this.page = page;
    }

    public void draw() {
        drawPage(new PageData(0, table.getRows().size()));
    }

    protected void drawPage(PageData pageData) {
        drawerList.forEach(drawer -> drawWithFunction(pageData, new Point2D.Float(this.startX, this.startY), drawer));
    }

    protected Queue<PageData> computeRowsOnPagesWithNewPageStartOf(float yOffsetOnNewPage) {
        final Queue<PageData> dataForPages = new LinkedList<>();
        float y = startY;
        int firstRowOnPage = 0;
        int lastRowOnPage = 0;
        for (final Row row : table.getRows()) {
            if (isRowTooHighToBeDrawnOnPage(row, yOffsetOnNewPage)) {
                throw new RowIsTooHighException("There is a row that is too high to be drawn on a single page");
            }
            if (isNotDrawableOnPage(y, row)) {
                dataForPages.add(new PageData(firstRowOnPage, lastRowOnPage));
                y = yOffsetOnNewPage;
                firstRowOnPage = lastRowOnPage;
            }
            y -= row.getHeight();
            lastRowOnPage++;
        }
        dataForPages.add(new PageData(firstRowOnPage, lastRowOnPage));
        return dataForPages;
    }

    private boolean isRowTooHighToBeDrawnOnPage(Row row, float yOffsetOnNewPage) {
        return row.getHeight() > (yOffsetOnNewPage - endY);
    }

    protected void determinePageToStartTable(float yOffsetOnNewPage) {
        if (startY - table.getRows().get(0).getHeight() < endY) {
            startY = yOffsetOnNewPage;
            startTableInNewPage = true;
        }
    }

    public void draw(Supplier<PDDocument> documentSupplier, Supplier<PDPage> pageSupplier, float yOffset) throws IOException {
        PDDocument document = documentSupplier.get();
        float startOnNewPage = pageSupplier.get().getMediaBox().getHeight() - yOffset;
        determinePageToStartTable(startOnNewPage);
        final Queue<PageData> pageDataQueue = computeRowsOnPagesWithNewPageStartOf(startOnNewPage);
        for (int i = 0; !pageDataQueue.isEmpty(); i++) {
            final PDPage pageToDrawOn = determinePageToDraw(i, document, pageSupplier);
            if ((i == 0 && startTableInNewPage) || i > 0 || document.getNumberOfPages() == 0) {
                startTableInNewPage = false;
            }
            if (i == 0) {
                tableStartPage = pageToDrawOn;
            }
            try (PDPageContentStream newPageContentStream = new PDPageContentStream(document, pageToDrawOn, APPEND, compress)) {
                setContentStream(newPageContentStream);
                setPage(pageToDrawOn);
                drawPage(pageDataQueue.poll());
            }
            setStartY(pageToDrawOn.getMediaBox().getHeight() - yOffset);
        }
    }

    protected PDPage determinePageToDraw(int index, PDDocument document, Supplier<PDPage> pageSupplier) {
        final PDPage pageToDrawOn;
        if ((index == 0 && startTableInNewPage) || index > 0 || document.getNumberOfPages() == 0) {
            pageToDrawOn = pageSupplier.get();
            document.addPage(pageToDrawOn);
        } else {
            pageToDrawOn = document.getPage(document.getNumberOfPages() - 1);
        }
        return pageToDrawOn;
    }

    protected void drawWithFunction(PageData pageData, Point2D.Float startingPoint, BiConsumer<Renderer, RenderContext> consumer) {
        float y = startingPoint.y;
        for (int rowIndex = pageData.firstRowOnPage; rowIndex < pageData.firstRowOnNextPage; rowIndex++) {
            final Row row = table.getRows().get(rowIndex);
            y -= row.getHeight();
            drawRow(new Point2D.Float(startingPoint.x, y), row, rowIndex, consumer);
        }
    }

    protected void drawRow(Point2D.Float start, Row row, int rowIndex, BiConsumer<Renderer, RenderContext> consumer) {
        float x = start.x;
        int columnCounter = 0;
        for (Cell cell : row.getCells()) {
            while (table.isRowSpanAt(rowIndex, columnCounter)) {
                x += table.getColumns().get(columnCounter).getWidth();
                columnCounter++;
            }
            consumer.accept(cell.getRenderer(), new RenderContext(pdDocument, contentStream, page, new Point2D.Float(x, start.y)));
            x += cell.getWidth();
            columnCounter += cell.getColSpan();
        }
    }

    private boolean isNotDrawableOnPage(float startY, Row row) {
        return startY - getHighestCellOf(row) < endY;
    }

    private Float getHighestCellOf(Row row) {
        return row.getCells().stream()
                .map(Cell::getHeight)
                .max(Comparator.naturalOrder())
                .orElse(row.getHeight());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Table table;

        private PDDocument pdDocument;

        private PDPageContentStream contentStream;

        private float startX;

        private float startY;

        private float endY;

        private Builder() {
        }

        public Builder table(Table table) {
            this.table = table;
            return this;
        }

        public Builder document(PDDocument document) {
            this.pdDocument = document;
            return this;
        }

        public Builder contentStream(PDPageContentStream contentStream) {
            this.contentStream = contentStream;
            return this;
        }

        public Builder startX(float startX) {
            this.startX = startX;
            return this;
        }

        public Builder startY(float startY) {
            this.startY = startY;
            return this;
        }

        public Builder endY(float endY) {
            this.endY = endY;
            return this;
        }

        public TableRenderer build() {
            TableRenderer tableRenderer = new TableRenderer(table);
            tableRenderer.setDocument(pdDocument);
            tableRenderer.setContentStream(contentStream);
            tableRenderer.setStartX(startX);
            tableRenderer.setStartY(startY);
            tableRenderer.setEndY(endY);
            return tableRenderer;
        }
    }

    public static class PageData {

        public final int firstRowOnPage;

        public final int firstRowOnNextPage;

        public PageData(int firstRowOnPage, int firstRowOnNextPage) {
            this.firstRowOnPage = firstRowOnPage;
            this.firstRowOnNextPage = firstRowOnNextPage;
        }
    }
}
