package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class LineTo extends PathCommand {

    public float x = 0f;
    public float y = 0f;

    public LineTo() {
    }

    public LineTo(boolean isRelative, float x, float y) {
        super(isRelative);
        this.x = x;
        this.y = y;
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        float offx = isRelative ? hist.lastPoint.x : 0f;
        float offy = isRelative ? hist.lastPoint.y : 0f;
        path.lineTo(x + offx, y + offy);
        hist.setLastPoint(x + offx, y + offy);
        hist.setLastKnot(x + offx, y + offy);
    }

    @Override
    public int getNumKnotsAdded() {
        return 2;
    }

    @Override
    public String toString() {
        return "L " + x + " " + y;
    }
}
