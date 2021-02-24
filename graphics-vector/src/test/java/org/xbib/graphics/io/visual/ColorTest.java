package org.xbib.graphics.io.visual;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

public class ColorTest extends AbstractTest {

    public ColorTest() throws IOException {
    }

    @Override
    public void draw(Graphics2D g) {
        final float wPage = (float) getPageSize().getWidth();
        final float hPage = (float) getPageSize().getHeight();
        final float wTile = Math.min(wPage / 15f, hPage / 15f);
        final float hTile = wTile;
        float w = wPage - wTile;
        float h = hPage - hTile;
        for (float y = (hPage - h) / 2f; y < h; y += hTile) {
            float yRel = y / h;
            for (float x = (wPage - w) / 2f; x < w; x += wTile) {
                float xRel = x / w;
                Color c = Color.getHSBColor(yRel, 1f, 1f);
                int alpha = 255 - (int) (xRel * 255f);
                g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
                g.fill(new Rectangle2D.Float(x, y, wTile, hTile));
            }
        }
    }
}
