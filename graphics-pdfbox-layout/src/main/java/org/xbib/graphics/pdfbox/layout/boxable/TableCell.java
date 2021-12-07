package org.xbib.graphics.pdfbox.layout.boxable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.xbib.graphics.pdfbox.layout.util.PDStreamUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TableCell<T extends PDPage> extends Cell<T> {

    private final String tableData;
    private final float width;
    private float yStart;
    private float xStart;
    private float height = 0;
    private final PDDocument doc;
    private final PDPage page;
    private final float marginBetweenElementsY = FontUtils.getHeight(getFont(), getFontSize());
    private final HorizontalAlignment align;
    private final VerticalAlignment valign;

    // default FreeSans font
//	private PDFont font = FontUtils.getDefaultfonts().get("font");
//	private PDFont fontBold = FontUtils.getDefaultfonts().get("fontBold");
    private PageContentStreamOptimized tableCellContentStream;

    // page margins
    private final float pageTopMargin;
    private final float pageBottomMargin;
    // default title fonts
    private final int tableTitleFontSize = 8;

    TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
              float yStart, float pageTopMargin, float pageBottomMargin) throws IOException {
        this(row, width, tableData, isCalculated, document, page, yStart, pageTopMargin, pageBottomMargin,
                HorizontalAlignment.LEFT, VerticalAlignment.TOP);
    }

    TableCell(Row<T> row, float width, String tableData, boolean isCalculated, PDDocument document, PDPage page,
              float yStart, float pageTopMargin, float pageBottomMargin, final HorizontalAlignment align,
              final VerticalAlignment valign) throws IOException {
        super(row, width, tableData, isCalculated);
        this.tableData = tableData;
        this.width = width * row.getWidth() / 100;
        this.doc = document;
        this.page = page;
        this.yStart = yStart;
        this.pageTopMargin = pageTopMargin;
        this.pageBottomMargin = pageBottomMargin;
        this.align = align;
        this.valign = valign;
        fillTable();
    }

    /**
     * <p>
     * This method just fills up the table's with her content for proper table
     * cell height calculation. Position of the table (x,y) is not relevant
     * here.
     * </p>
     * <p>
     * NOTE: if entire row is not header row then use bold instead header cell (
     * {@code
     *
     * <th>})
     * </p>
     */
    public void fillTable() throws IOException {
        // please consider the cell's paddings
        float tableWidth = this.width - getLeftPadding() - getRightPadding();
        tableCellContentStream = new PageContentStreamOptimized(new PDPageContentStream(doc, page,
                PDPageContentStream.AppendMode.APPEND, true));
        // check if there is some additional text outside inner table
        String[] outerTableText = tableData.split("<table");
        // don't forget to attach splited tag
        for (int i = 1; i < outerTableText.length; i++) {
            outerTableText[i] = "<table " + outerTableText[i];
        }
        Paragraph outerTextParagraph = null;
        String caption = "";
        height = 0;
        height = (getTopBorder() == null ? 0 : getTopBorder().getWidth()) + getTopPadding();
        for (String element : outerTableText) {
            if (element.contains("</table")) {
                String[] chunks = element.split("</table>");
                for (String chunkie : chunks) {

                    // make paragraph and get tokens
                    outerTextParagraph = new Paragraph(chunkie, getFont(), 8, (int) tableWidth);
                    outerTextParagraph.getLines();
                    height += (outerTextParagraph != null
                            ? outerTextParagraph.getHeight() + marginBetweenElementsY : 0);
                    yStart = writeOrCalculateParagraph(outerTextParagraph, true);
                }
            } else {
                // make paragraph and get tokens
                outerTextParagraph = new Paragraph(element, getFont(), 8, (int) tableWidth);
                outerTextParagraph.getLines();
                height += (outerTextParagraph != null ? outerTextParagraph.getHeight() + marginBetweenElementsY
                        : 0);
                yStart = writeOrCalculateParagraph(outerTextParagraph, true);
            }
        }
        tableCellContentStream.close();

    }

    /**
     * <p>
     * Method provides writing or height calculation of possible outer text
     * </p>
     *
     * @param paragraph           Paragraph that needs to be written or whose height needs to be
     *                            calculated
     * @param onlyCalculateHeight if <code>true</code> the given paragraph will not be drawn
     *                            just his height will be calculated.
     * @return Y position after calculating/writing given paragraph
     */
    private float writeOrCalculateParagraph(Paragraph paragraph, boolean onlyCalculateHeight) throws IOException {
        int boldCounter = 0;
        int italicCounter = 0;

        if (!onlyCalculateHeight) {
            tableCellContentStream.setRotated(isTextRotated());
        }

        // position at top of current cell descending by font height - font
        // descent, because we are positioning the base line here
        float cursorY = yStart - getTopPadding() - FontUtils.getHeight(getFont(), getFontSize())
                - FontUtils.getDescent(getFont(), getFontSize()) - (getTopBorder() == null ? 0 : getTopBorder().getWidth());
        float cursorX = xStart;

        // loop through tokens
        for (Map.Entry<Integer, List<Token>> entry : paragraph.getMapLineTokens().entrySet()) {

            // calculate the width of this line
            float freeSpaceWithinLine = paragraph.getMaxLineWidth() - paragraph.getLineWidth(entry.getKey());
            if (isTextRotated()) {
                switch (align) {
                    case CENTER:
                        cursorY += freeSpaceWithinLine / 2;
                        break;
                    case LEFT:
                        break;
                    case RIGHT:
                        cursorY += freeSpaceWithinLine;
                        break;
                }
            } else {
                switch (align) {
                    case CENTER:
                        cursorX += freeSpaceWithinLine / 2;
                        break;
                    case LEFT:
                        // it doesn't matter because X position is always the same
                        // as row above
                        break;
                    case RIGHT:
                        cursorX += freeSpaceWithinLine;
                        break;
                }
            }

            // iterate through tokens in current line
            PDFont currentFont = paragraph.getFont(false, false);
            for (Token token : entry.getValue()) {
                switch (token.getType()) {
                    case OPEN_TAG:
                        if ("b".equals(token.getData())) {
                            boldCounter++;
                        } else if ("i".equals(token.getData())) {
                            italicCounter++;
                        }
                        break;
                    case CLOSE_TAG:
                        if ("b".equals(token.getData())) {
                            boldCounter = Math.max(boldCounter - 1, 0);
                        } else if ("i".equals(token.getData())) {
                            italicCounter = Math.max(italicCounter - 1, 0);
                        }
                        break;
                    case PADDING:
                        cursorX += Float.parseFloat(token.getData());
                        break;
                    case ORDERING:
                        currentFont = paragraph.getFont(boldCounter > 0, italicCounter > 0);
                        tableCellContentStream.setFont(currentFont, getFontSize());
                        if (isTextRotated()) {
                            // if it is not calculation then draw it
                            if (!onlyCalculateHeight) {
                                tableCellContentStream.newLineAt(cursorX, cursorY);
                                tableCellContentStream.showText(token.getData());
                            }
                            cursorY += token.getWidth(currentFont) / 1000 * getFontSize();
                        } else {
                            // if it is not calculation then draw it
                            if (!onlyCalculateHeight) {
                                tableCellContentStream.newLineAt(cursorX, cursorY);
                                tableCellContentStream.showText(token.getData());
                            }
                            cursorX += token.getWidth(currentFont) / 1000 * getFontSize();
                        }
                        break;
                    case BULLET:
                        float widthOfSpace = currentFont.getSpaceWidth();
                        float halfHeight = FontUtils.getHeight(currentFont, getFontSize()) / 2;
                        if (isTextRotated()) {
                            if (!onlyCalculateHeight) {
                                PDStreamUtils.rect(tableCellContentStream, cursorX + halfHeight, cursorY,
                                        token.getWidth(currentFont) / 1000 * getFontSize(),
                                        widthOfSpace / 1000 * getFontSize(), getTextColor());
                            }
                            // move cursorY for two characters (one for bullet, one
                            // for space after bullet)
                            cursorY += 2 * widthOfSpace / 1000 * getFontSize();
                        } else {
                            if (!onlyCalculateHeight) {
                                PDStreamUtils.rect(tableCellContentStream, cursorX, cursorY + halfHeight,
                                        token.getWidth(currentFont) / 1000 * getFontSize(),
                                        widthOfSpace / 1000 * getFontSize(), getTextColor());
                            }
                            // move cursorX for two characters (one for bullet, one
                            // for space after bullet)
                            cursorX += 2 * widthOfSpace / 1000 * getFontSize();
                        }
                        break;
                    case TEXT:
                        currentFont = paragraph.getFont(boldCounter > 0, italicCounter > 0);
                        tableCellContentStream.setFont(currentFont, getFontSize());
                        if (isTextRotated()) {
                            if (!onlyCalculateHeight) {
                                tableCellContentStream.newLineAt(cursorX, cursorY);
                                tableCellContentStream.showText(token.getData());
                            }
                            cursorY += token.getWidth(currentFont) / 1000 * getFontSize();
                        } else {
                            if (!onlyCalculateHeight) {
                                tableCellContentStream.newLineAt(cursorX, cursorY);
                                tableCellContentStream.showText(token.getData());
                            }
                            cursorX += token.getWidth(currentFont) / 1000 * getFontSize();
                        }
                        break;
                }
            }
            // reset
            cursorX = xStart;
            cursorY -= FontUtils.getHeight(getFont(), getFontSize());
        }
        return cursorY;
    }

    /**
     * <p>
     * This method draw table cell with proper X,Y position which are determined
     * in {@link Table#draw()} method
     * </p>
     * <p>
     * NOTE: if entire row is not header row then use bold instead header cell (
     * {@code
     *
     * <th>})
     * </p>
     *
     * @param page {@link PDPage} where table cell be written on
     */
    public void draw(PDPage page) throws IOException {
        // please consider the cell's paddings
        float tableWidth = this.width - getLeftPadding() - getRightPadding();
        tableCellContentStream = new PageContentStreamOptimized(new PDPageContentStream(doc, page,
                PDPageContentStream.AppendMode.APPEND, true));
        // check if there is some additional text outside inner table
        String[] outerTableText = tableData.split("<table");
        // don't forget to attach splited tag
        for (int i = 1; i < outerTableText.length; i++) {
            outerTableText[i] = "<table " + outerTableText[i];
        }
        Paragraph outerTextParagraph = null;
        String caption = "";
        height = 0;
        height = (getTopBorder() == null ? 0 : getTopBorder().getWidth()) + getTopPadding();
        for (String element : outerTableText) {
            if (element.contains("</table")) {
                String[] chunks = element.split("</table>");
                for (String chunkie : chunks) {

                    // make paragraph and get tokens
                    outerTextParagraph = new Paragraph(chunkie, getFont(), 8, (int) tableWidth);
                    outerTextParagraph.getLines();
                    height += (outerTextParagraph != null
                            ? outerTextParagraph.getHeight() + marginBetweenElementsY : 0);
                    yStart = writeOrCalculateParagraph(outerTextParagraph, false);
                }
            } else {
                // make paragraph and get tokens
                outerTextParagraph = new Paragraph(element, getFont(), 8, (int) tableWidth);
                outerTextParagraph.getLines();
                height += (outerTextParagraph != null ? outerTextParagraph.getHeight() + marginBetweenElementsY
                        : 0);
                yStart = writeOrCalculateParagraph(outerTextParagraph, false);
            }
        }
        tableCellContentStream.close();
    }

    public float getXPosition() {
        return xStart;
    }

    public void setXPosition(float xStart) {
        this.xStart = xStart;
    }

    public float getYPosition() {
        return yStart;
    }

    public void setYPosition(float yStart) {
        this.yStart = yStart;
    }

    @Override
    public float getTextHeight() {
        return height;
    }

    @Override
    public float getHorizontalFreeSpace() {
        return getInnerWidth() - width;
    }

    @Override
    public float getVerticalFreeSpace() {
        return getInnerHeight() - width;
    }

}