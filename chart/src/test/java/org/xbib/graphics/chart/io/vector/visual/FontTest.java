package org.xbib.graphics.chart.io.vector.visual;

import org.xbib.graphics.chart.io.vector.GraphicsState;

import java.awt.Font;
import java.awt.Graphics2D;

public class FontTest extends AbstractTest {

    @Override
    public void draw(Graphics2D g) {
        final int tileCountH = 4;
        final int tileCountV = 8;
        final double wTile = getPageSize().width / tileCountH;
        final double hTile = getPageSize().height / tileCountV;
        final double xOrigin = (getPageSize().width - tileCountH * wTile) / 2.0;
        final double yOrigin = (getPageSize().height - tileCountV * hTile) / 2.0;
        double x = xOrigin;
        double y = yOrigin;

        final float[] sizes = {
                GraphicsState.DEFAULT_FONT.getSize2D(), GraphicsState.DEFAULT_FONT.getSize2D() / 2f
        };
        final String[] names = {
                GraphicsState.DEFAULT_FONT.getName(), Font.SERIF, Font.MONOSPACED, "Monospaced"
        };
        final int[] styles = {
                Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC
        };

        for (float size : sizes) {
            for (String name : names) {
                for (int style : styles) {
                    Font font = new Font(name, style, 10).deriveFont(size);
                    g.setFont(font);
                    g.drawString("vg2d", (float) x, (float) y);

                    x += wTile;
                    if (x >= tileCountH * wTile) {
                        x = xOrigin;
                        y += hTile;
                    }
                }
            }
        }
    }
}
