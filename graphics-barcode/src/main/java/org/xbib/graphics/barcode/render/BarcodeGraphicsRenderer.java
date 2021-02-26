package org.xbib.graphics.barcode.render;

import org.xbib.graphics.barcode.HumanReadableLocation;
import org.xbib.graphics.barcode.Symbol;
import org.xbib.graphics.barcode.util.Hexagon;
import org.xbib.graphics.barcode.util.TextBox;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders symbologies using the Java Graphics API.
 */
public class BarcodeGraphicsRenderer {

    /**
     * The graphics to render to.
     */
    private final Graphics2D g2d;

    private final Rectangle rectangle;

    /**
     * The scaling factor.
     */
    private final double scalingFactor;

    /**
     * The paper (background) color.
     */
    private final Color background;

    /**
     * The ink (foreground) color.
     */
    private final Color foreground;

    private final boolean antialias;

    private final boolean transparentBackground;

    /**
     * Creates a new Java 2D renderer.
     *
     * @param g2d           the graphics to render to
     * @param rectangle the visible rectangle
     * @param scalingFactor the scaling factor to apply
     * @param background         the paper (background) color
     * @param foreground           the ink (foreground) color
     * @param antialias if true give anti alias hint
     * @param transparentBackground if true background should be transparent
     */
    public BarcodeGraphicsRenderer(Graphics2D g2d,
                                   Rectangle rectangle,
                                   double scalingFactor,
                                   Color background,
                                   Color foreground,
                                   boolean antialias,
                                   boolean transparentBackground) {
        this.g2d = g2d;
        this.rectangle = rectangle;
        this.scalingFactor = scalingFactor;
        this.background = background;
        this.foreground = foreground;
        this.antialias = antialias;
        this.transparentBackground = transparentBackground;
    }

    public void render(Symbol symbol) {
        RenderingHints oldRenderingHints = g2d.getRenderingHints();
        Color oldColor = g2d.getColor();
        Color oldBackground = g2d.getBackground();
        if (antialias) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
        double marginX = symbol.getQuietZoneHorizontal() * scalingFactor;
        double marginY = symbol.getQuietZoneVertical() * scalingFactor;
        g2d.setBackground(background);
        if (!transparentBackground) {
            g2d.setColor(background);
            if (rectangle != null) {
                g2d.fill(rectangle);
            }
            g2d.setColor(foreground);
        }
        for (Rectangle2D.Double rect : symbol.getRectangles()) {
            double x = rect.x * scalingFactor + marginX;
            double y = rect.y * scalingFactor + marginY;
            double w = rect.width * scalingFactor;
            double h = rect.height * scalingFactor;
            Path2D path = new Path2D.Double();
            path.moveTo(x, y);
            path.lineTo(x + w, y);
            path.lineTo(x + w, y + h);
            path.lineTo(x, y + h);
            path.closePath();
            g2d.fill(path);
        }
        if (symbol.getHumanReadableLocation() != HumanReadableLocation.NONE) {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.TRACKING, 0);
            Font f = new Font(symbol.getFontName(), Font.PLAIN, (int) (symbol.getFontSize() * scalingFactor)).deriveFont(attributes);
            Font oldFont = g2d.getFont();
            g2d.setFont(f);
            FontMetrics fm = g2d.getFontMetrics();
            for (TextBox text : symbol.getTexts()) {
                Rectangle2D bounds = fm.getStringBounds(text.text, g2d);
                double x = (text.x * scalingFactor) - (bounds.getWidth() / 2) + marginX;
                double y = (text.y * scalingFactor) + marginY;
                g2d.drawString(text.text, (float) x, (float) y);
            }
            g2d.setFont(oldFont);
        }
        for (Hexagon hexagon : symbol.getHexagons()) {
            Path2D path = new Path2D.Double();
            path.moveTo(hexagon.pointX[0] * scalingFactor + marginX, hexagon.pointY[0] * scalingFactor + marginY);
            for(int i = 1; i < 6; ++i) {
                double x = hexagon.pointX[i] * scalingFactor + marginX;
                double y = hexagon.pointY[i] * scalingFactor + marginY;
                path.lineTo(x, y);
            }
            path.closePath();
            g2d.fill(path);
        }
        List<Ellipse2D.Double> targets = symbol.getTarget();
        for (int i = 0; i < targets.size(); i++) {
            Ellipse2D.Double ellipse = targets.get(i);
            double x = ellipse.x * scalingFactor + marginX;
            double y = ellipse.y * scalingFactor + marginY;
            double w = ellipse.width * scalingFactor;
            double h = ellipse.height * scalingFactor;
            if ((i & 1) == 0) {
                g2d.setColor(foreground);
            } else {
                g2d.setColor(background);
            }
            g2d.fill(new Ellipse2D.Double(x, y, w, h));
        }
        g2d.setColor(oldColor);
        g2d.setBackground(oldBackground);
        g2d.setRenderingHints(oldRenderingHints);
    }

    public void close() {
        g2d.dispose();
    }
}
