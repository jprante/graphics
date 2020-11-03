package org.xbib.graphics.chart.io.vector.visual;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

public class StrokeTest extends AbstractTest {

    private static final Stroke[] strokes = {
            // Width
            new BasicStroke(0.0f),
            new BasicStroke(0.5f),
            new BasicStroke(1.0f),
            new BasicStroke(2.0f),
            // Cap
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER),
            new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER),
            new BasicStroke(1f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER),
            null,
            // Join
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND),
            null,
            // Miter limit
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 2f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 3f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f),
            // Dash pattern
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{1f}, 0f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{1f, 1f}, 0f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f, 1f}, 0f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f, 4f, 1f}, 0f),
            // Dash phase
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f}, 0.5f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f}, 1.0f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f}, 1.5f),
            new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 1f}, 2.5f),
    };

    @Override
    public void draw(Graphics2D g) {
        final int tileCountH = 4;
        final int tileCountV = 6;
        final double wTile = getPageSize().width / tileCountH;
        final double hTile = getPageSize().height / tileCountV;
        final double xOrigin = (getPageSize().width - tileCountH * wTile) / 2.0;
        final double yOrigin = (getPageSize().height - tileCountV * hTile) / 2.0;

        final Path2D path = new Path2D.Double();
        path.moveTo(0.00, 0.00);
        path.lineTo(0.33, 1.00);
        path.lineTo(0.67, 0.00);
        path.quadTo(0.33, 0.00, 0.33, 0.50);
        path.quadTo(0.33, 1.00, 0.67, 1.00);
        path.quadTo(1.00, 1.00, 1.00, 0.50);
        path.lineTo(0.67, 0.50);
        path.moveTo(1.0, 0.4);
        path.curveTo(1.0, 0.3, 1.0, 0.0, 1.2, 0.0);
        path.curveTo(1.3, 0.0, 1.4, 0.1, 1.4, 0.3);
        path.curveTo(1.4, 0.5, 1.2, 0.8, 1.0, 1.0);
        path.lineTo(1.6, 1.0);
        path.lineTo(1.6, 0.0);
        path.curveTo(1.8, 0.0, 2.0, 0.2, 2.0, 0.5);
        path.curveTo(2.0, 0.6, 2.0, 0.8, 1.9, 0.9);

        path.transform(AffineTransform.getScaleInstance(0.8 * wTile / 2.0, 0.6 * hTile));

        double x = xOrigin;
        double y = yOrigin;
        for (Stroke stroke : strokes) {
            if (stroke != null) {
                Path2D p = new Path2D.Double(path);
                p.transform(AffineTransform.getTranslateInstance(x, y));

                g.setStroke(stroke);
                g.draw(p);
            }

            x += wTile;
            if (x >= tileCountH * wTile) {
                x = xOrigin;
                y += hTile;
            }
        }
    }
}
