package org.xbib.graphics.svg.util;

import org.xbib.graphics.svg.element.shape.Marker;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

public class MarkerLayout {

    private final List<MarkerPos> markerList = new ArrayList<>();

    boolean started = false;

    public void layout(Shape shape) {
        double px = 0;
        double py = 0;
        double[] coords = new double[6];
        for (PathIterator it = shape.getPathIterator(null);
             !it.isDone(); it.next()) {
            switch (it.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                    px = coords[0];
                    py = coords[1];
                    started = false;
                    break;
                case PathIterator.SEG_CLOSE:
                    started = false;
                    break;
                case PathIterator.SEG_LINETO: {
                    double x = coords[0];
                    double y = coords[1];
                    markerIn(px, py, x - px, y - py);
                    markerOut(x, y, x - px, y - py);
                    px = x;
                    py = y;
                    break;
                }
                case PathIterator.SEG_QUADTO: {
                    double k0x = coords[0];
                    double k0y = coords[1];
                    double x = coords[2];
                    double y = coords[3];
                    if (px != k0x || py != k0y) {
                        markerIn(px, py, k0x - px, k0y - py);
                    } else {
                        markerIn(px, py, x - px, y - py);
                    }
                    if (x != k0x || y != k0y) {
                        markerOut(x, y, x - k0x, y - k0y);
                    } else {
                        markerOut(x, y, x - px, y - py);
                    }
                    markerIn(px, py, k0x - px, k0y - py);
                    markerOut(x, y, x - k0x, y - k0y);
                    px = x;
                    py = y;
                    break;
                }
                case PathIterator.SEG_CUBICTO: {
                    double k0x = coords[0];
                    double k0y = coords[1];
                    double k1x = coords[2];
                    double k1y = coords[3];
                    double x = coords[4];
                    double y = coords[5];
                    if (px != k0x || py != k0y) {
                        markerIn(px, py, k0x - px, k0y - py);
                    } else if (px != k1x || py != k1y) {
                        markerIn(px, py, k1x - px, k1y - py);
                    } else {
                        markerIn(px, py, x - px, y - py);
                    }
                    if (x != k1x || y != k1y) {
                        markerOut(x, y, x - k1x, y - k1y);
                    } else if (x != k0x || y != k0y) {
                        markerOut(x, y, x - k0x, y - k0y);
                    } else {
                        markerOut(x, y, x - px, y - py);
                    }
                    px = x;
                    py = y;
                    break;
                }
            }
        }
        for (int i = 1; i < markerList.size(); ++i) {
            MarkerPos prev = markerList.get(i - 1);
            MarkerPos cur = markerList.get(i);
            if (cur.getType() == Marker.MARKER_START) {
                prev.setType(Marker.MARKER_END);
            }
        }
        MarkerPos last = markerList.get(markerList.size() - 1);
        last.setType(Marker.MARKER_END);
    }

    private void markerIn(double x, double y, double dx, double dy) {
        if (!started) {
            started = true;
            markerList.add(new MarkerPos(Marker.MARKER_START, x, y, dx, dy));
        }
    }

    private void markerOut(double x, double y, double dx, double dy) {
        markerList.add(new MarkerPos(Marker.MARKER_MID, x, y, dx, dy));
    }

    public List<MarkerPos> getMarkerList() {
        return markerList;
    }
}
