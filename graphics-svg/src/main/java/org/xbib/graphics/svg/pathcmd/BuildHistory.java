package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.Point2D;

public class BuildHistory {

    private final Point2D.Float startPoint = new Point2D.Float();

    private final Point2D.Float lastPoint = new Point2D.Float();

    private final Point2D.Float lastKnot = new Point2D.Float();

    public void setStartPoint(float x, float y) {
        startPoint.setLocation(x, y);
    }

    public Point2D.Float getStartPoint() {
        return startPoint;
    }

    public void setLastPoint(float x, float y) {
        lastPoint.setLocation(x, y);
    }

    public Point2D.Float getLastPoint() {
        return lastPoint;
    }

    public void setLastKnot(float x, float y) {
        lastKnot.setLocation(x, y);
    }

    public Point2D.Float getLastKnot() {
        return lastKnot;
    }
}
