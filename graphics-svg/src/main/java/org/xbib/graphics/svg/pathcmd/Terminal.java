package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.GeneralPath;

public class Terminal extends PathCommand {

    @Override
    public void appendPath(GeneralPath path, BuildHistory hist) {
        path.closePath();
        hist.setLastPoint(hist.getStartPoint().x, hist.getStartPoint().y);
        hist.setLastKnot(hist.getStartPoint().x, hist.getStartPoint().y);
    }

    @Override
    public int getNumKnotsAdded() {
        return 0;
    }

    @Override
    public String toString() {
        return "Z";
    }
}
