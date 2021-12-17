package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Horizontal extends PathCommand {

    public float x = 0f;

    public Horizontal() {
    }

    @Override
    public String toString() {
        return "H " + x;
    }

    public Horizontal(boolean isRelative, float x) {
        super(isRelative);
        this.x = x;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = hist.lastPoint.y;
        path.lineTo(x + offx, offy);
        hist.setLastPoint(x + offx, offy);
        hist.setLastKnot(x + offx, offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 2;
    }
}
