package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Quadratic extends PathCommand {

    public float kx = 0f;

    public float ky = 0f;

    public float x = 0f;

    public float y = 0f;

    public Quadratic() {
    }

    public Quadratic(boolean isRelative, float kx, float ky, float x, float y) {
        super(isRelative);
        this.kx = kx;
        this.ky = ky;
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = isRelative ? hist.lastPoint.y : 0f;
        path.quadTo(kx + offx, ky + offy, x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(kx + offx, ky + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 4;
    }

    @Override
    public String toString() {
        return "Q " + kx + " " + ky
                + " " + x + " " + y;
    }
}
