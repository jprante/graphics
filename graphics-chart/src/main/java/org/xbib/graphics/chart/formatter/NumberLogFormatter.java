package org.xbib.graphics.chart.formatter;

import org.xbib.graphics.chart.axis.Direction;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;

@SuppressWarnings("serial")
public class NumberLogFormatter extends Format {

    private final AxesChartStyler styler;

    private final Direction axisDirection;

    private final NumberFormat numberFormat;

    public NumberLogFormatter(AxesChartStyler styler, Direction axisDirection) {
        this.styler = styler;
        this.axisDirection = axisDirection;
        numberFormat = NumberFormat.getNumberInstance(styler.getLocale());
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        double number = (Double) obj;
        String decimalPattern;
        if (axisDirection == Direction.X && styler.getXAxisDecimalPattern() != null) {
            decimalPattern = styler.getXAxisDecimalPattern();
        } else if (axisDirection == Direction.Y && styler.getYAxisDecimalPattern() != null) {
            decimalPattern = styler.getYAxisDecimalPattern();
        } else if (styler.getDecimalPattern() != null) {
            decimalPattern = styler.getDecimalPattern();
        } else {
            if (Math.abs(number) > 1000.0 || Math.abs(number) < 0.001) {
                decimalPattern = "0E0";
            } else {
                decimalPattern = "0.###";
            }
        }
        DecimalFormat normalFormat = (DecimalFormat) numberFormat;
        normalFormat.applyPattern(decimalPattern);
        toAppendTo.append(normalFormat.format(number));
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
