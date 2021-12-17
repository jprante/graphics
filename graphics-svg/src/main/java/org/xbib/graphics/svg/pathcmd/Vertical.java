package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Vertical extends PathCommand {

    public float y = 0f;

    public Vertical() {
    }

    @Override
    public String toString() {
        return "V " + y;
    }

    public Vertical(boolean isRelative, float y) {
        super(isRelative);
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = hist.lastPoint.x;
        float offy = isRelative ? hist.lastPoint.y : 0f;
        path.lineTo(offx, y + offy);
        hist.setLastPoint(offx, y + offy);
        hist.setLastKnot(offx, y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 2;
    }
}
