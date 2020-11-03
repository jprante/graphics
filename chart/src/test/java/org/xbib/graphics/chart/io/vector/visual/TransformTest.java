package org.xbib.graphics.chart.io.vector.visual;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class TransformTest extends AbstractTest {

    @Override
    public void draw(Graphics2D g) {
        final int rowCount = 2;
        final int colCount = 4;
        double wTile = getPageSize().width / colCount;
        double hTile = wTile;

        g.translate(0.5 * wTile, 0.5 * hTile);
        AffineTransform txOrig = g.getTransform();

        Shape s = new Rectangle2D.Double(0.0, 0.0, 0.5 * wTile, 0.75 * hTile);

        // Row 1

        g.draw(s);

        g.translate(wTile, 0.0);
        g.draw(s);

        g.translate(wTile, 0.0);
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.scale(0.5, 0.5);
            g2.draw(s);
            g2.dispose();
        }

        g.translate(wTile, 0.0);
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(Math.toRadians(30.0));
            g2.draw(s);
            g2.dispose();
        }

        // Row 2

        g.setTransform(txOrig);
        g.translate(0.0, hTile);

        g.shear(0.5, 0.0);
        g.draw(s);
        g.shear(-0.5, 0.0);
        g.translate(wTile, 0.0);

        g.shear(0.0, 0.5);
        g.draw(s);
        g.shear(0.0, -0.5);
    }
}
