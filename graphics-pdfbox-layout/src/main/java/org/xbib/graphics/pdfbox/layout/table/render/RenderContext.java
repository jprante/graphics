package org.xbib.graphics.pdfbox.layout.table.render;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.awt.geom.Point2D;

public class RenderContext {

     private final PDPageContentStream contentStream;

     private final PDPage page;

     private final Point2D.Float startingPoint;

     public RenderContext(PDPageContentStream contentStream, PDPage page, Point2D.Float startingPoint) {
          this.contentStream = contentStream;
          this.page = page;
          this.startingPoint = startingPoint;
     }

     public PDPageContentStream getContentStream() {
          return contentStream;
     }

     public PDPage getPage() {
          return page;
     }

     public Point2D.Float getStartingPoint() {
          return startingPoint;
     }
}
