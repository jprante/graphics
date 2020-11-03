package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.formatter.NumberLogFormatter;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.math.BigDecimal;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis
 * ticks for logarithmic axes
 */
public class AxisTickCalculatorLogarithmic extends AxisTickCalculator {

    private final NumberLogFormatter numberLogFormatter;

    public AxisTickCalculatorLogarithmic(Direction axisDirection, double workingSpace, double minValue, double maxValue, AxesChartStyler styler) {
        super(axisDirection, workingSpace, minValue, maxValue, styler);
        numberLogFormatter = new NumberLogFormatter(styler, axisDirection);
        axisFormat = numberLogFormatter;
        calculate();
    }

    private void calculate() {
        if (minValue == maxValue) {
            tickLabels.add(numberLogFormatter.format(BigDecimal.valueOf(maxValue)));
            tickLocations.add(workingSpace / 2.0);
            return;
        }
        double tickSpace = styler.getPlotContentSize() * workingSpace;
        if (tickSpace < styler.getXAxisTickMarkSpacingHint()) {
            return;
        }
        double margin = (workingSpace - tickSpace) / 2.0;
        int logMin = (int) Math.floor(Math.log10(minValue));
        int logMax = (int) Math.ceil(Math.log10(maxValue));
        double firstPosition = pow(logMin);
        double tickStep = pow(logMin - 1);
        for (int i = logMin; i <= logMax; i++) {
            for (double j = firstPosition; j <= pow(i) + .00000001; j = j + tickStep) {
                if (j < minValue - tickStep) {
                    continue;
                }
                if (j > maxValue + tickStep) {
                    break;
                }
                if (Math.abs(Math.log10(j) % 1) < 0.00000001) {
                    tickLabels.add(numberLogFormatter.format(j));
                } else {
                    tickLabels.add(null);
                }
                double tickLabelPosition = (int) (margin + (Math.log10(j) - Math.log10(minValue)) / (Math.log10(maxValue) - Math.log10(minValue)) * tickSpace);
                tickLocations.add(tickLabelPosition);
            }
            tickStep = tickStep * pow(1);
            firstPosition = tickStep + pow(i);
        }
    }

    private static double pow(int exponent) {
        return exponent > 0 ? Math.pow(10, exponent) : 1.0 / Math.pow(10, -1 * exponent);
    }
}
