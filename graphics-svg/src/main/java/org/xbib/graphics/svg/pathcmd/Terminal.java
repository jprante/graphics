package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Terminal extends PathCommand {

    public Terminal() {
    }

    @Override
    public String toString() {
        return "Z";
    }

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        path.closePath();
        hist.setLastPoint(hist.startPoint.x, hist.startPoint.y);
        hist.setLastKnot(hist.startPoint.x, hist.startPoint.y);
    }

    @Override
    public int getNumKnotsAdded() {
        return 0;
    }
}
