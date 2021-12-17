package org.xbib.graphics.svg.xml;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StyleAttribute {

    private static final Logger logger = Logger.getLogger(StyleAttribute.class.getName());

    static final Pattern patternUrl = Pattern.compile("\\s*url\\((.*)\\)\\s*");

    static final Matcher matchFpNumUnits = Pattern.compile("\\s*([-+]?((\\d*\\.\\d+)|(\\d+))([-+]?[eE]\\d+)?)\\s*(px|cm|mm|in|pc|pt|em|ex)\\s*").matcher("");

    String name;

    String stringValue;

    public StyleAttribute() {
        this(null, null);
    }

    public StyleAttribute(String name) {
        this.name = name;
        stringValue = null;
    }

    public StyleAttribute(String name, String stringValue) {
        this.name = name;
        this.stringValue = stringValue;
    }

    public String getName() {
        return name;
    }

    public StyleAttribute setName(String name) {
        this.name = name;
        return this;
    }

    public String getStringValue() {
        return stringValue;
    }

    public String[] getStringList() {
        return XMLParseUtil.parseStringList(stringValue);
    }

    public void setStringValue(String value) {
        stringValue = value;
    }

    public boolean getBooleanValue() {
        return stringValue.equalsIgnoreCase("true");
    }

    public int getIntValue() {
        return XMLParseUtil.findInt(stringValue);
    }

    public int[] getIntList() {
        return XMLParseUtil.parseIntList(stringValue);
    }

    public double getDoubleValue() {
        return XMLParseUtil.findDouble(stringValue);
    }

    public double[] getDoubleList() {
        return XMLParseUtil.parseDoubleList(stringValue);
    }

    public float getFloatValue() {
        return XMLParseUtil.findFloat(stringValue);
    }

    public float[] getFloatList() {
        return XMLParseUtil.parseFloatList(stringValue);
    }

    public float getRatioValue() {
        return (float) XMLParseUtil.parseRatio(stringValue);
    }

    public String getUnits() {
        matchFpNumUnits.reset(stringValue);
        if (!matchFpNumUnits.matches()) return null;
        return matchFpNumUnits.group(6);
    }

    public NumberWithUnits getNumberWithUnits() {
        return XMLParseUtil.parseNumberWithUnits(stringValue);
    }

    public float getFloatValueWithUnits() {
        NumberWithUnits number = getNumberWithUnits();
        return convertUnitsToPixels(number.getUnits(), number.getValue());
    }

    public float[] getFloatListWithUnits() {
        String[] values = getStringList();
        float[] result = new float[values.length];
        for (int i = 0; i < result.length; i++) {
            NumberWithUnits number = XMLParseUtil.parseNumberWithUnits(stringValue);
            result[i] = convertUnitsToPixels(number.getUnits(), number.getValue());
        }
        return result;
    }

    static public float convertUnitsToPixels(int unitType, float value) {
        if (unitType == NumberWithUnits.UT_UNITLESS || unitType == NumberWithUnits.UT_PERCENT) {
            return value;
        }
        float pixPerInch;
        try {
            pixPerInch = (float) Toolkit.getDefaultToolkit().getScreenResolution();
        } catch (HeadlessException ex) {
            pixPerInch = 72;
        }
        final float inchesPerCm = .3936f;
        switch (unitType) {
            case NumberWithUnits.UT_IN:
                return value * pixPerInch;
            case NumberWithUnits.UT_CM:
                return value * inchesPerCm * pixPerInch;
            case NumberWithUnits.UT_MM:
                return value * .1f * inchesPerCm * pixPerInch;
            case NumberWithUnits.UT_PT:
                return value * (1f / 72f) * pixPerInch;
            case NumberWithUnits.UT_PC:
                return value * (1f / 6f) * pixPerInch;
        }
        return value;
    }

    public Color getColorValue() {
        return ColorTable.parseColor(stringValue);
    }

    public String parseURLFn() {
        Matcher matchUrl = patternUrl.matcher(stringValue);
        if (!matchUrl.matches()) {
            return null;
        }
        return matchUrl.group(1);
    }

    public URL getURLValue(URL docRoot) {
        String fragment = parseURLFn();
        if (fragment == null) return null;
        try {
            return new URL(docRoot, fragment);
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }

    public URI getURIValue() {
        return getURIValue(null);
    }

    public URI getURIValue(URI base) {
        try {
            String fragment = parseURLFn();
            if (fragment == null) {
                fragment = stringValue.replaceAll("\\s+", "");
            }
            if (Pattern.matches("[a-zA-Z]:!\\\\.*", fragment)) {
                File file = new File(fragment);
                return file.toURI();
            }
            URI uriFrag = new URI(fragment);
            if (uriFrag.isAbsolute()) {
                return uriFrag;
            }
            if (base == null) return uriFrag;
            URI relBase = new URI(null, base.getSchemeSpecificPart(), null);
            URI relUri;
            if (relBase.isOpaque()) {
                relUri = new URI(null, base.getSchemeSpecificPart(), uriFrag.getFragment());
            } else {
                relUri = relBase.resolve(uriFrag);
            }
            return new URI(base.getScheme() + ":" + relUri);
        } catch (Exception e) {
            logger.log(Level.WARNING, null, e);
            return null;
        }
    }
}
