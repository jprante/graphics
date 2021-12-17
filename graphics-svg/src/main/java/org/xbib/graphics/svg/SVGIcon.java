package org.xbib.graphics.svg;

import javax.swing.ImageIcon;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URI;

@SuppressWarnings("serial")
public class SVGIcon extends ImageIcon {

    public static final String PROP_AUTOSIZE = "PROP_AUTOSIZE";

    private final PropertyChangeSupport changes = new PropertyChangeSupport(this);

    SVGUniverse svgUniverse = new SVGUniverse();

    public static final int INTERP_NEAREST_NEIGHBOR = 0;

    public static final int INTERP_BILINEAR = 1;

    public static final int INTERP_BICUBIC = 2;

    private boolean antiAlias;
    private int interpolation = INTERP_NEAREST_NEIGHBOR;
    private boolean clipToViewbox;

    URI svgURI;

    AffineTransform scaleXform = new AffineTransform();

    public static final int AUTOSIZE_NONE = 0;

    public static final int AUTOSIZE_HORIZ = 1;

    public static final int AUTOSIZE_VERT = 2;

    public static final int AUTOSIZE_BESTFIT = 3;

    public static final int AUTOSIZE_STRETCH = 4;

    private int autosize = AUTOSIZE_NONE;

    Dimension preferredSize;

    public SVGIcon() {
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        changes.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        changes.removePropertyChangeListener(p);
    }

    @Override
    public Image getImage() {
        BufferedImage bi = new BufferedImage(getIconWidth(), getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        paintIcon(null, bi.getGraphics(), 0, 0);
        return bi;
    }

    public int getIconHeightIgnoreAutosize() {
        if (preferredSize != null &&
                (autosize == AUTOSIZE_VERT || autosize == AUTOSIZE_STRETCH
                        || autosize == AUTOSIZE_BESTFIT)) {
            return preferredSize.height;
        }
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null) {
            return 0;
        }
        return (int) diagram.getHeight();
    }

    public int getIconWidthIgnoreAutosize() {
        if (preferredSize != null &&
                (autosize == AUTOSIZE_HORIZ || autosize == AUTOSIZE_STRETCH
                        || autosize == AUTOSIZE_BESTFIT)) {
            return preferredSize.width;
        }
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null) {
            return 0;
        }
        return (int) diagram.getWidth();
    }

    private boolean isAutoSizeBestFitUseFixedHeight(final int iconWidthIgnoreAutosize, final int iconHeightIgnoreAutosize,
                                                    final SVGDiagram diagram) {
        return iconHeightIgnoreAutosize / diagram.getHeight() < iconWidthIgnoreAutosize / diagram.getWidth();
    }

    @Override
    public int getIconWidth() {
        final int iconWidthIgnoreAutosize = getIconWidthIgnoreAutosize();
        final int iconHeightIgnoreAutosize = getIconHeightIgnoreAutosize();
        final SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (preferredSize != null && (autosize == AUTOSIZE_VERT ||
                (autosize == AUTOSIZE_BESTFIT && isAutoSizeBestFitUseFixedHeight(iconWidthIgnoreAutosize, iconHeightIgnoreAutosize, diagram)))) {
            final double aspectRatio = diagram.getHeight() / diagram.getWidth();
            return (int) (iconHeightIgnoreAutosize / aspectRatio);
        } else {
            return iconWidthIgnoreAutosize;
        }
    }

    @Override
    public int getIconHeight() {
        final int iconWidthIgnoreAutosize = getIconWidthIgnoreAutosize();
        final int iconHeightIgnoreAutosize = getIconHeightIgnoreAutosize();
        final SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (preferredSize != null && (autosize == AUTOSIZE_HORIZ ||
                (autosize == AUTOSIZE_BESTFIT && !isAutoSizeBestFitUseFixedHeight(iconWidthIgnoreAutosize, iconHeightIgnoreAutosize, diagram)))) {
            final double aspectRatio = diagram.getHeight() / diagram.getWidth();
            return (int) (iconWidthIgnoreAutosize * aspectRatio);
        } else {
            return iconHeightIgnoreAutosize;
        }
    }

    @Override
    public void paintIcon(Component comp, Graphics gg, int x, int y) {
        Graphics2D g = (Graphics2D) gg.create();
        try {
            paintIcon(comp, g, x, y);
        } catch (SVGException | IOException e) {
            throw new RuntimeException(e);
        }
        g.dispose();
    }

    private void paintIcon(Component comp, Graphics2D g, int x, int y) throws SVGException, IOException {
        Object oldAliasHint = g.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antiAlias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        Object oldInterpolationHint = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        switch (interpolation) {
            case INTERP_NEAREST_NEIGHBOR:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                break;
            case INTERP_BILINEAR:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                break;
            case INTERP_BICUBIC:
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                break;
        }
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram == null) {
            return;
        }
        g.translate(x, y);
        diagram.setIgnoringClipHeuristic(!clipToViewbox);
        if (clipToViewbox) {
            g.setClip(new Rectangle2D.Float(0, 0, diagram.getWidth(), diagram.getHeight()));
        }
        if (autosize == AUTOSIZE_NONE) {
            diagram.render(g);
            g.translate(-x, -y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
            return;
        }
        final int width = getIconWidthIgnoreAutosize();
        final int height = getIconHeightIgnoreAutosize();
        if (width == 0 || height == 0) {
            return;
        }
        double diaWidth = diagram.getWidth();
        double diaHeight = diagram.getHeight();

        double scaleW = 1;
        double scaleH = 1;
        if (autosize == AUTOSIZE_BESTFIT) {
            scaleW = scaleH = Math.min(height / diaHeight, width / diaWidth);
        } else if (autosize == AUTOSIZE_HORIZ) {
            scaleW = scaleH = width / diaWidth;
        } else if (autosize == AUTOSIZE_VERT) {
            scaleW = scaleH = height / diaHeight;
        } else if (autosize == AUTOSIZE_STRETCH) {
            scaleW = width / diaWidth;
            scaleH = height / diaHeight;
        }
        scaleXform.setToScale(scaleW, scaleH);
        AffineTransform oldXform = g.getTransform();
        g.transform(scaleXform);
        diagram.render(g);
        g.setTransform(oldXform);
        g.translate(-x, -y);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAliasHint);
        if (oldInterpolationHint != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolationHint);
        }
    }

    public SVGUniverse getSvgUniverse() {
        return svgUniverse;
    }

    public void setSvgUniverse(SVGUniverse svgUniverse) {
        SVGUniverse old = this.svgUniverse;
        this.svgUniverse = svgUniverse;
        changes.firePropertyChange("svgUniverse", old, svgUniverse);
    }

    public URI getSvgURI() {
        return svgURI;
    }

    public void setSvgURI(URI svgURI) {
        URI old = this.svgURI;
        this.svgURI = svgURI;
        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram != null) {
            Dimension size = getPreferredSize();
            if (size == null) {
                size = new Dimension((int) diagram.getRoot().getDeviceWidth(), (int) diagram.getRoot().getDeviceHeight());
            }
            diagram.setDeviceViewport(new Rectangle(0, 0, size.width, size.height));
        }
        changes.firePropertyChange("svgURI", old, svgURI);
    }

    public void setSvgResourcePath(String resourcePath) {
        URI old = this.svgURI;
        try {
            svgURI = new URI(getClass().getResource(resourcePath).toString());
            changes.firePropertyChange("svgURI", old, svgURI);
            SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
            if (diagram != null) {
                diagram.setDeviceViewport(new Rectangle(0, 0, preferredSize.width, preferredSize.height));
            }
        } catch (Exception e) {
            svgURI = old;
        }
    }

    public boolean isScaleToFit() {
        return autosize == AUTOSIZE_STRETCH;
    }

    public void setScaleToFit(boolean scaleToFit) {
        setAutosize(AUTOSIZE_STRETCH);
    }

    public Dimension getPreferredSize() {
        if (preferredSize == null) {
            SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
            if (diagram != null) {
                setPreferredSize(new Dimension((int) diagram.getWidth(), (int) diagram.getHeight()));
            }
        }
        return new Dimension(preferredSize);
    }

    public void setPreferredSize(Dimension preferredSize) {
        Dimension old = this.preferredSize;
        this.preferredSize = preferredSize;

        SVGDiagram diagram = svgUniverse.getDiagram(svgURI);
        if (diagram != null) {
            diagram.setDeviceViewport(new Rectangle(0, 0, preferredSize.width, preferredSize.height));
        }

        changes.firePropertyChange("preferredSize", old, preferredSize);
    }

    public boolean getUseAntiAlias() {
        return getAntiAlias();
    }

    public void setUseAntiAlias(boolean antiAlias) {
        setAntiAlias(antiAlias);
    }

    public boolean getAntiAlias() {
        return antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        boolean old = this.antiAlias;
        this.antiAlias = antiAlias;
        changes.firePropertyChange("antiAlias", old, antiAlias);
    }

    public int getInterpolation() {
        return interpolation;
    }

    public void setInterpolation(int interpolation) {
        int old = this.interpolation;
        this.interpolation = interpolation;
        changes.firePropertyChange("interpolation", old, interpolation);
    }

    public boolean isClipToViewbox() {
        return clipToViewbox;
    }

    public void setClipToViewbox(boolean clipToViewbox) {
        this.clipToViewbox = clipToViewbox;
    }

    public int getAutosize() {
        return autosize;
    }

    public void setAutosize(int autosize) {
        int oldAutosize = this.autosize;
        this.autosize = autosize;
        changes.firePropertyChange(PROP_AUTOSIZE, oldAutosize, autosize);
    }
}
