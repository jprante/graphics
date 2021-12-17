package org.xbib.graphics.svg.pathcmd;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

public class PathUtil {

    public PathUtil() {
    }

    public static String buildPathString(GeneralPath path) {
        float[] coords = new float[6];
        StringBuilder sb = new StringBuilder();
        for (PathIterator pathIt = path.getPathIterator(new AffineTransform()); !pathIt.isDone(); pathIt.next()) {
            int segId = pathIt.currentSegment(coords);
            switch (segId) {
                case PathIterator.SEG_CLOSE: {
                    sb.append(" Z");
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    sb.append(" C " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " " + coords[5]);
                    break;
                }
                case PathIterator.SEG_LINETO: {
                    sb.append(" L " + coords[0] + " " + coords[1]);
                    break;
                }
                case PathIterator.SEG_MOVETO: {
                    sb.append(" M " + coords[0] + " " + coords[1]);
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    sb.append(" Q " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3]);
                    break;
                }
            }
        }
        return sb.toString();
    }
}
