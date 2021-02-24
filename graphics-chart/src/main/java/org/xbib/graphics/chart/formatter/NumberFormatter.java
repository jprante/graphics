package org.xbib.graphics.chart.formatter;

import org.xbib.graphics.chart.axis.Direction;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;

@SuppressWarnings("serial")
public class NumberFormatter extends Format {

    private final AxesChartStyler styler;
    private final Direction axisDirection;
    private final double min;
    private final double max;
    private final NumberFormat numberFormat;

    public NumberFormatter(AxesChartStyler styler, Direction axisDirection, double min, double max) {
        this.styler = styler;
        this.axisDirection = axisDirection;
        this.min = min;
        this.max = max;
        numberFormat = NumberFormat.getNumberInstance(styler.getLocale());
    }

    public String getFormatPattern(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }
        double difference = max - min;
        int placeOfDifference;
        if (difference == 0.0) {
            placeOfDifference = 0;
        } else {
            placeOfDifference = (int) Math.floor(Math.log(difference) / Math.log(10));
        }
        int placeOfValue;
        if (value.doubleValue() == 0.0) {
            placeOfValue = 0;
        } else {
            placeOfValue = (int) Math.floor(Math.log(value.doubleValue()) / Math.log(10));
        }
        if (placeOfDifference <= 4 && placeOfDifference >= -4) {
            return getNormalDecimalPatternPositive(placeOfValue);
        } else {
            return getScientificDecimalPattern();
        }
    }

    private String getNormalDecimalPatternPositive(int placeOfValue) {
        int maxNumPlaces = 15;
        StringBuilder sb = new StringBuilder();
        for (int i = maxNumPlaces - 1; i >= -1 * maxNumPlaces; i--) {
            if (i >= 0 && (i < placeOfValue)) {
                sb.append("0");
            } else if (i < 0 && (i > placeOfValue)) {
                sb.append("0");
            } else {
                sb.append("#");
            }
            if (i % 3 == 0 && i > 0) {
                sb.append(",");
            }
            if (i == 0) {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    private String getScientificDecimalPattern() {
        return "0.###############E0";
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        Number value = (Number) obj;
        String decimalPattern;
        if (axisDirection == Direction.X && styler.getXAxisDecimalPattern() != null) {
            decimalPattern = styler.getXAxisDecimalPattern();
        } else if (axisDirection == Direction.Y && styler.getYAxisDecimalPattern() != null) {
            decimalPattern = styler.getYAxisDecimalPattern();
        } else if (styler.getDecimalPattern() != null) {
            decimalPattern = styler.getDecimalPattern();
        } else {
            decimalPattern = getFormatPattern(BigDecimal.valueOf(value.doubleValue()));
        }
        DecimalFormat normalFormat = (DecimalFormat) numberFormat;
        normalFormat.applyPattern(decimalPattern);
        toAppendTo.append(normalFormat.format(value));
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
