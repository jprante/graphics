package org.xbib.graphics.pdfbox.layout.test.table;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.layout.font.BaseFont;
import org.xbib.graphics.pdfbox.layout.table.AbstractCell;
import org.xbib.graphics.pdfbox.layout.table.BorderStyle;
import org.xbib.graphics.pdfbox.layout.table.Column;
import org.xbib.graphics.pdfbox.layout.table.HorizontalAlignment;
import org.xbib.graphics.pdfbox.layout.table.ImageCell;
import org.xbib.graphics.pdfbox.layout.table.PdfUtil;
import org.xbib.graphics.pdfbox.layout.table.Row;
import org.xbib.graphics.pdfbox.layout.table.Table;
import org.xbib.graphics.pdfbox.layout.table.TableRenderer;
import org.xbib.graphics.pdfbox.layout.table.TableNotYetBuiltException;
import org.xbib.graphics.pdfbox.layout.table.TextCell;
import java.awt.Color;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TableTest {

    @Test
    public void minimalExample() throws Exception {
        try (PDDocument document = new PDDocument()) {
            final PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                Table myTable = Table.builder()
                        .addColumnsOfWidth(200, 200)
                        .padding(2)
                        .addRow(Row.builder()
                                .add(TextCell.builder().text("One One").borderWidth(4).backgroundColor(Color.WHITE).build())
                                .add(TextCell.builder().text("One Two").borderWidth(0).backgroundColor(Color.YELLOW).build())
                                .build())
                        .addRow(Row.builder()
                                .padding(10)
                                .add(TextCell.builder().text("Two One").textColor(Color.RED).build())
                                .add(TextCell.builder().text("Two Two")
                                        .borderWidthRight(1f)
                                        .borderStyleRight(BorderStyle.DOTTED)
                                        .horizontalAlignment(HorizontalAlignment.RIGHT)
                                        .build())
                                .build())
                        .build();
                TableRenderer tableRenderer = TableRenderer.builder()
                        .table(myTable)
                        .contentStream(contentStream)
                        .startX(20f)
                        .startY(page.getMediaBox().getUpperRightY() - 20f)
                        .build();
                tableRenderer.draw();
            }
            document.save("build/table-minimal-example.pdf");
        }
    }

    @Test
    public void getNumberOfColumnsTableBuilderWithThreeColumns() {
        final Table.Builder tableBuilder = Table.builder()
                .addColumnOfWidth(12)
                .addColumnOfWidth(34)
                .addColumnOfWidth(56);
        final Table table = tableBuilder.build();
        assertThat(table.getNumberOfColumns(), equalTo(3));
    }


    @Test
    public void getWidthTableBuilderWithTwoColumns() {
        final Table.Builder tableBuilder = Table.builder()
                .addColumnOfWidth(20)
                .addColumnOfWidth(40);
        final Table table = tableBuilder.build();
        assertThat(table.getWidth(), equalTo(60f));
    }


    @Test
    public void getRowsTableBuilderWithOneRow() {
        final Table.Builder tableBuilder = Table.builder();
        tableBuilder.addColumnOfWidth(12)
                .addColumnOfWidth(34);
        final Row row = Row.builder()
                .add(TextCell.builder().text("11").build())
                .add(TextCell.builder().text("12").build())
                .build();
        tableBuilder.addRow(row);
        final Table table = tableBuilder.build();
        assertThat(table.getRows().size(), equalTo(1));
    }

    @Test
    public void getHeightTwoRowsWithDifferentPaddings() {
        final Table table = Table.builder()
                .addColumnOfWidth(12)
                .addColumnOfWidth(34)
                .fontSize(12)
                .addRow(Row.builder()
                        .add(TextCell.builder().text("11").paddingTop(35).paddingBottom(15).build())
                        .add(TextCell.builder().text("12").paddingTop(15).paddingBottom(25).build())
                        .build())
                .build();
        final float actualFontHeight = PdfUtil.getFontHeight(table.getSettings().getFont(), 12);
        assertThat(table.getHeight(), equalTo(50 + actualFontHeight));
    }

    @Test
    public void tableBuilderShouldConnectStructureCorrectly() {
        AbstractCell lastCell = TextCell.builder().text("").build();
        Table table = Table.builder()
                .addColumnOfWidth(10)
                .addColumnOfWidth(20)
                .addColumnOfWidth(30)
                .addRow(Row.builder()
                        .add(TextCell.builder().text("").colSpan(2).build())
                        .add(lastCell)
                        .build())
                .build();
        Column lastColumn = table.getColumns().get(table.getColumns().size() - 1);
        assertThat(lastCell.getColumn(), is(lastColumn));
    }

    @Test
    public void getHeightShouldThrowExceptionIfNotYetRendered() {
        Assertions.assertThrows(TableNotYetBuiltException.class, () -> {
            Row row = Row.builder()
                    .add(TextCell.builder().text("This text should break because too long").colSpan(2).borderWidth(1).build())
                    .add(TextCell.builder().text("Booz").build())
                    .wordBreak(true)
                    .font(BaseFont.COURIER).fontSize(8)
                    .build();
            row.getHeight();
        });
    }

    @Test
    public void getHeightShouldReturnValueIfTableIsBuilt() {
        Table.Builder tableBuilder = Table.builder()
                .addColumnsOfWidth(10, 10, 10)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .fontSize(10).font(BaseFont.HELVETICA)
                .wordBreak(false);
        Row row = Row.builder()
                .add(TextCell.builder().text("iVgebALheQlBkxtDyNDrhKv").colSpan(2).borderWidth(1).build())
                .add(TextCell.builder().text("Booz").build())
                .font(BaseFont.COURIER).fontSize(8)
                .build();
        tableBuilder.addRow(row);
        tableBuilder.build();
        assertThat(row.getHeight(), greaterThan(0f));
    }

    @Test
    public void getHeightShouldThrowExceptionIfTableNotYetBuilt() throws Exception {
        Assertions.assertThrows(TableNotYetBuiltException.class, () ->{
            byte[] bytes = Files.readAllBytes(Paths.get("src/test/resources/org/xbib/graphics/pdfbox/layout/test/cat.jpg"));
            PDImageXObject image = PDImageXObject.createFromByteArray(new PDDocument(), bytes, "test1");
            AbstractCell cell = ImageCell.builder().image(image).build();
            cell.getHeight();
        });
    }
}
