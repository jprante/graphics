package org.xbib.graphics.svg.element.gradient;

import org.xbib.graphics.svg.SVGElementException;
import org.xbib.graphics.svg.SVGException;
import org.xbib.graphics.svg.SVGLoaderHelper;
import org.xbib.graphics.svg.Stop;
import org.xbib.graphics.svg.element.FillElement;
import org.xbib.graphics.svg.element.SVGElement;
import org.xbib.graphics.svg.xml.StyleAttribute;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public abstract class Gradient extends FillElement {

    public static final String TAG_NAME = "gradient";

    public static final int SM_PAD = 0;

    public static final int SM_REPEAT = 1;

    public static final int SM_REFLECT = 2;

    public int spreadMethod = SM_PAD;

    public static final int GU_OBJECT_BOUNDING_BOX = 0;

    public static final int GU_USER_SPACE_ON_USE = 1;

    protected int gradientUnits = GU_OBJECT_BOUNDING_BOX;

    List<Stop> stops = new ArrayList<>();

    URI stopRef = null;

    protected AffineTransform gradientTransform = null;

    float[] stopFractions;

    Color[] stopColors;

    public Gradient() {
    }

    @Override
    public String getTagName() {
        return TAG_NAME;
    }

    @Override
    public void loaderAddChild(SVGLoaderHelper helper, SVGElement child) throws SVGElementException {
        super.loaderAddChild(helper, child);
        if (!(child instanceof Stop)) {
            return;
        }
        appendStop((Stop) child);
    }

    @Override
    protected void build() throws SVGException, IOException {
        super.build();
        StyleAttribute sty = new StyleAttribute();
        String strn;
        if (getPres(sty.setName("spreadMethod"))) {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("repeat")) {
                spreadMethod = SM_REPEAT;
            } else if (strn.equals("reflect")) {
                spreadMethod = SM_REFLECT;
            } else {
                spreadMethod = SM_PAD;
            }
        }
        if (getPres(sty.setName("gradientUnits"))) {
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                gradientUnits = GU_USER_SPACE_ON_USE;
            } else {
                gradientUnits = GU_OBJECT_BOUNDING_BOX;
            }
        }
        if (getPres(sty.setName("gradientTransform"))) {
            gradientTransform = parseTransform(sty.getStringValue());
        }
        if (gradientTransform == null) {
            gradientTransform = new AffineTransform();
        }
        if (getPres(sty.setName("xlink:href"))) {
            try {
                stopRef = sty.getURIValue(getXMLBase());
            } catch (Exception e) {
                throw new SVGException("Could not resolve relative URL in Gradient: " + sty.getStringValue() + ", " + getXMLBase(), e);
            }
        }
    }

    private void buildStops() {
        ArrayList<Stop> stopList = new ArrayList<>(stops);
        stopList.sort((o1, o2) -> Float.compare(o1.offset, o2.offset));
        for (int i = stopList.size() - 2; i >= 0; --i) {
            if (stopList.get(i + 1).offset == stopList.get(i).offset) {
                stopList.remove(i + 1);
            }
        }
        stopFractions = new float[stopList.size()];
        stopColors = new Color[stopList.size()];
        int idx = 0;
        for (Stop stop : stopList) {
            int stopColorVal = stop.color.getRGB();
            Color stopColor = new Color((stopColorVal >> 16) & 0xff, (stopColorVal >> 8) & 0xff, stopColorVal & 0xff, clamp((int) (stop.opacity * 255), 0, 255));
            stopColors[idx] = stopColor;
            stopFractions[idx] = stop.offset;
            idx++;
        }
    }

    public float[] getStopFractions() {
        if (stopRef != null) {
            Gradient grad = (Gradient) diagram.getUniverse().getElement(stopRef);
            return grad.getStopFractions();
        }
        if (stopFractions != null) {
            return stopFractions;
        }
        buildStops();
        return stopFractions;
    }

    public Color[] getStopColors() {
        if (stopRef != null) {
            Gradient grad = (Gradient) diagram.getUniverse().getElement(stopRef);
            return grad.getStopColors();
        }
        if (stopColors != null) {
            return stopColors;
        }
        buildStops();
        return stopColors;
    }

    private int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public void setStopRef(URI grad) {
        stopRef = grad;
    }

    public void appendStop(Stop stop) {
        stops.add(stop);
    }

    @Override
    public boolean updateTime(double curTime) throws SVGException {
        boolean stateChange = false;
        StyleAttribute sty = new StyleAttribute();
        String strn;
        if (getPres(sty.setName("spreadMethod"))) {
            int newVal;
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("repeat")) {
                newVal = SM_REPEAT;
            } else if (strn.equals("reflect")) {
                newVal = SM_REFLECT;
            } else {
                newVal = SM_PAD;
            }
            if (spreadMethod != newVal) {
                spreadMethod = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("gradientUnits"))) {
            int newVal;
            strn = sty.getStringValue().toLowerCase();
            if (strn.equals("userspaceonuse")) {
                newVal = GU_USER_SPACE_ON_USE;
            } else {
                newVal = GU_OBJECT_BOUNDING_BOX;
            }
            if (newVal != gradientUnits) {
                gradientUnits = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("gradientTransform"))) {
            AffineTransform newVal = parseTransform(sty.getStringValue());
            if (newVal.equals(gradientTransform)) {
                gradientTransform = newVal;
                stateChange = true;
            }
        }
        if (getPres(sty.setName("xlink:href"))) {
            URI newVal = sty.getURIValue(getXMLBase());
            if ((newVal == null && stopRef != null) || !newVal.equals(stopRef)) {
                stopRef = newVal;
                stateChange = true;
            }
        }
        for (Stop stop : stops) {
            if (stop.updateTime(curTime)) {
                stateChange = true;
                stopFractions = null;
                stopColors = null;
            }
        }
        return stateChange;
    }
}
