package org.xbib.graphics.svg.element;

import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.element.shape.Group;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class Symbol extends Group {

    private AffineTransform viewXform;

    private Rectangle2D viewBox;

    @Override
    public String getTagName() {
        return "symbol";
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        if (getPres(sty.setName("viewBox"))) {
            float[] dim = sty.getFloatList();
            viewBox = new Rectangle2D.Float(dim[0], dim[1], dim[2], dim[3]);
        }
        if (viewBox == null) {
            viewBox = new Rectangle(0, 0, 1, 1);
        }
        viewXform = new AffineTransform();
        viewXform.scale(1.0 / viewBox.getWidth(), 1.0 / viewBox.getHeight());
        viewXform.translate(-viewBox.getX(), -viewBox.getY());
    }

    @Override
    protected boolean outsideClip(Graphics2D g) throws SVGException {
        Shape clip = g.getClip();
        Rectangle2D rect = super.getBoundingBox();
        return clip != null && !clip.intersects(rect);
    }

    @Override
    public void doRender(Graphics2D g) throws SVGException, IOException {
        AffineTransform oldXform = g.getTransform();
        g.transform(viewXform);
        super.doRender(g);
        g.setTransform(oldXform);
    }

    @Override
    public Shape getShape() {
        Shape shape = super.getShape();
        return viewXform.createTransformedShape(shape);
    }

    @Override
    public Rectangle2D getBoundingBox() throws SVGException {
        Rectangle2D rect = super.getBoundingBox();
        return viewXform.createTransformedShape(rect).getBounds2D();
    }
}
