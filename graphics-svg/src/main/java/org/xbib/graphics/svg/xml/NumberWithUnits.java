package org.xbib.graphics.svg.xml;

public class NumberWithUnits {

    public static final int UT_UNITLESS = 0;

    public static final int UT_PX = 1;  //Pixels

    public static final int UT_CM = 2;  //Centimeters

    public static final int UT_MM = 3;  //Millimeters

    public static final int UT_IN = 4;  //Inches

    public static final int UT_EM = 5;  //Default font height

    public static final int UT_EX = 6;  //Height of character 'x' in default font

    public static final int UT_PT = 7;  //Points - 1/72 of an inch

    public static final int UT_PC = 8;  //Picas - 1/6 of an inch

    public static final int UT_PERCENT = 9;  //Percent - relative width

    float value = 0f;

    int unitType = UT_UNITLESS;

    public NumberWithUnits(String value) {
        set(value);
    }

    public NumberWithUnits(float value, int unitType) {
        this.value = value;
        this.unitType = unitType;
    }

    public float getValue() {
        return value;
    }

    public int getUnits() {
        return unitType;
    }

    public void set(String value) {
        this.value = XMLParseUtil.findFloat(value);
        unitType = UT_UNITLESS;
        if (value.contains("px")) {
            unitType = UT_PX;
            return;
        }
        if (value.contains("cm")) {
            unitType = UT_CM;
            return;
        }
        if (value.contains("mm")) {
            unitType = UT_MM;
            return;
        }
        if (value.contains("in")) {
            unitType = UT_IN;
            return;
        }
        if (value.contains("em")) {
            unitType = UT_EM;
            return;
        }
        if (value.contains("ex")) {
            unitType = UT_EX;
            return;
        }
        if (value.contains("pt")) {
            unitType = UT_PT;
            return;
        }
        if (value.contains("pc")) {
            unitType = UT_PC;
            return;
        }
        if (value.contains("%")) {
            unitType = UT_PERCENT;
        }
    }

    public static String unitsAsString(int unitIdx) {
        switch (unitIdx) {
            default:
                return "";
            case UT_PX:
                return "px";
            case UT_CM:
                return "cm";
            case UT_MM:
                return "mm";
            case UT_IN:
                return "in";
            case UT_EM:
                return "em";
            case UT_EX:
                return "ex";
            case UT_PT:
                return "pt";
            case UT_PC:
                return "pc";
            case UT_PERCENT:
                return "%";
        }
    }

    @Override
    public String toString() {
        return "" + value + unitsAsString(unitType);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberWithUnits other = (NumberWithUnits) obj;
        if (Float.floatToIntBits(this.value) != Float.floatToIntBits(other.value)) {
            return false;
        }
        return this.unitType == other.unitType;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Float.floatToIntBits(this.value);
        hash = 37 * hash + this.unitType;
        return hash;
    }
}
