package org.xbib.graphics.pdfbox.layout.table;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.function.Supplier;

public class RepeatedHeaderTableRenderer extends TableRenderer {

	private int numberOfRowsToRepeat = 1;

	private Float headerHeight;

	public RepeatedHeaderTableRenderer(Table table) {
		super(table);
	}

	@Override
	protected void drawPage(PageData pageData) {
		if (pageData.firstRowOnPage != 0) {
			float adaption = 0;
			for (int i = 0; i < numberOfRowsToRepeat; i++) {
				adaption += table.getRows().get(i).getHeight();
				Point2D.Float startPoint = new Point2D.Float(this.startX,
						this.startY + calculateHeightForFirstRows() - adaption);
				drawRow(startPoint, table.getRows().get(i), i, (drawer, drawingContext) -> {
					drawer.renderBackground(drawingContext);
					drawer.renderContent(drawingContext);
					drawer.renderBorders(drawingContext);
				});
			}
		}

		drawerList.forEach(drawer -> drawWithFunction(pageData, new Point2D.Float(this.startX, this.startY), drawer));
	}

	@Override
	protected void determinePageToStartTable(float yOffsetOnNewPage) {
		float minimumRowsToFitHeight = 0;
		int minimumRowsToFit = table.getRows().size() > numberOfRowsToRepeat
				? numberOfRowsToRepeat + 1
				: numberOfRowsToRepeat;

		for (final Row row : table.getRows().subList(0, minimumRowsToFit))
			minimumRowsToFitHeight += row.getHeight();

		if (startY - minimumRowsToFitHeight < endY) {
			startY = yOffsetOnNewPage + calculateHeightForFirstRows();
			startTableInNewPage = true;
		}
	}

	@Override
	public void draw(Supplier<PDDocument> documentSupplier, Supplier<PDPage> pageSupplier, float yOffset)
			throws IOException {
		super.draw(documentSupplier, pageSupplier, yOffset + calculateHeightForFirstRows());
	}

	private float calculateHeightForFirstRows() {
		if (headerHeight != null) {
			return headerHeight;
		}

		float height = 0;
		for (int i = 0; i < numberOfRowsToRepeat; i++) {
			height += table.getRows().get(i).getHeight();
		}

		// Cache and return
		headerHeight = height;
		return height;
	}

}
