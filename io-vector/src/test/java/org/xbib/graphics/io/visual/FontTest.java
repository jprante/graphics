package org.xbib.graphics.io.visual;

import org.xbib.graphics.io.vector.GraphicsState;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.IOException;

public class FontTest extends AbstractTest {

    public FontTest() throws IOException {
    }

    @Override
    public void draw(Graphics2D g) {
        final int tileCountH = 4;
        final int tileCountV = 8;
        final double wTile = getPageSize().getWidth() / tileCountH;
        final double hTile = getPageSize().getHeight() / tileCountV;
        final double xOrigin = (getPageSize().getWidth() - tileCountH * wTile) / 2.0;
        final double yOrigin = (getPageSize().getHeight() - tileCountV * hTile) / 2.0;
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
