package org.xbib.graphics.svg.xml;

import org.xbib.graphics.svg.SVGConst;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLParseUtil {

    static final Matcher fpMatch = Pattern.compile("([-+]?((\\d*\\.\\d+)|(\\d+))([eE][+-]?\\d+)?)(\\%|in|cm|mm|pt|pc|px|em|ex)?").matcher("");

    static final Matcher intMatch = Pattern.compile("[-+]?\\d+").matcher("");

    static final Matcher quoteMatch = Pattern.compile("^'|'$").matcher("");

    private XMLParseUtil() {
    }

    public static String[] parseStringList(String list) {
        final Matcher matchWs = Pattern.compile("[^\\s]+").matcher("");
        matchWs.reset(list);
        List<String> matchList = new LinkedList<>();
        while (matchWs.find()) {
            matchList.add(matchWs.group());
        }
        String[] retArr = new String[matchList.size()];
        return matchList.toArray(retArr);
    }

    public static double parseDouble(String val) {
        return findDouble(val);
    }

    public synchronized static double findDouble(String val) {
        if (val == null) {
            return 0;
        }
        fpMatch.reset(val);
        try {
            if (!fpMatch.find()) {
                return 0;
            }
        } catch (StringIndexOutOfBoundsException e) {
            Logger.getLogger(SVGConst.SVG_LOGGER).log(Level.WARNING,
                    "XMLParseUtil: regex parse problem: '" + val + "'", e);
        }
        val = fpMatch.group(1);
        double retVal = 0;
        try {
            retVal = Double.parseDouble(val);

            float pixPerInch;
            try {
                pixPerInch = (float) Toolkit.getDefaultToolkit().getScreenResolution();
            } catch (NoClassDefFoundError err) {
                pixPerInch = 72;
            }
            final float inchesPerCm = .3936f;
            final String units = fpMatch.group(6);

            if ("%".equals(units)) retVal /= 100;
            else if ("in".equals(units)) {
                retVal *= pixPerInch;
            } else if ("cm".equals(units)) {
                retVal *= inchesPerCm * pixPerInch;
            } else if ("mm".equals(units)) {
                retVal *= inchesPerCm * pixPerInch * .1f;
            } else if ("pt".equals(units)) {
                retVal *= (1f / 72f) * pixPerInch;
            } else if ("pc".equals(units)) {
                retVal *= (1f / 6f) * pixPerInch;
            }
        } catch (Exception e) {
            //
        }
        return retVal;
    }

    public synchronized static double[] parseDoubleList(String list) {
        if (list == null) {
            return null;
        }
        fpMatch.reset(list);
        List<Double> doubList = new LinkedList<>();
        while (fpMatch.find()) {
            String val = fpMatch.group(1);
            doubList.add(Double.valueOf(val));
        }
        double[] retArr = new double[doubList.size()];
        Iterator<Double> it = doubList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            retArr[idx++] = it.next();
        }
        return retArr;
    }

    public synchronized static float findFloat(String val) {
        if (val == null) {
            return 0f;
        }
        fpMatch.reset(val);
        if (!fpMatch.find()) {
            return 0f;
        }
        val = fpMatch.group(1);
        float retVal = 0f;
        try {
            retVal = Float.parseFloat(val);
            String units = fpMatch.group(6);
            if ("%".equals(units)) retVal /= 100;
        } catch (Exception e) {
            //
        }
        return retVal;
    }

    public synchronized static float[] parseFloatList(String list) {
        if (list == null) {
            return null;
        }
        fpMatch.reset(list);
        List<Float> floatList = new LinkedList<>();
        while (fpMatch.find()) {
            String val = fpMatch.group(1);
            floatList.add(Float.valueOf(val));
        }
        float[] retArr = new float[floatList.size()];
        Iterator<Float> it = floatList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            retArr[idx++] = it.next();
        }
        return retArr;
    }

    public static int findInt(String val) {
        if (val == null) {
            return 0;
        }
        intMatch.reset(val);
        if (!intMatch.find()) {
            return 0;
        }
        val = intMatch.group();
        int retVal = 0;
        try {
            retVal = Integer.parseInt(val);
        } catch (Exception e) {
            //
        }
        return retVal;
    }

    public static int[] parseIntList(String list) {
        if (list == null) {
            return null;
        }
        intMatch.reset(list);
        List<Integer> intList = new LinkedList<>();
        while (intMatch.find()) {
            String val = intMatch.group();
            intList.add(Integer.valueOf(val));
        }
        int[] retArr = new int[intList.size()];
        Iterator<Integer> it = intList.iterator();
        int idx = 0;
        while (it.hasNext()) {
            retArr[idx++] = it.next();
        }
        return retArr;
    }

    public static double parseRatio(String val) {
        if (val == null || val.equals("")) return 0.0;

        if (val.charAt(val.length() - 1) == '%') {
            parseDouble(val.substring(0, val.length() - 1));
        }
        return parseDouble(val);
    }

    public static NumberWithUnits parseNumberWithUnits(String val) {
        if (val == null) return null;

        return new NumberWithUnits(val);
    }

    public static HashMap<String, StyleAttribute> parseStyle(String styleString, HashMap<String, StyleAttribute> map) {
        final Pattern patSemi = Pattern.compile(";");
        String[] styles = patSemi.split(styleString);
        for (String style : styles) {
            if (style.length() == 0) {
                continue;
            }
            int colon = style.indexOf(':');
            if (colon == -1) {
                continue;
            }
            String key = style.substring(0, colon).trim().intern();
            String value = quoteMatch.reset(style.substring(colon + 1).trim()).replaceAll("").intern();
            map.put(key, new StyleAttribute(key, value));
        }
        return map;
    }
}
