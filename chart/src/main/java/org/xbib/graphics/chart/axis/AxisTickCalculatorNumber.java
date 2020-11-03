package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.formatter.NumberFormatter;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis
 * ticks for decimal axes
 */
public class AxisTickCalculatorNumber extends AxisTickCalculator {

    private final NumberFormatter numberFormatter;

    public AxisTickCalculatorNumber(Direction axisDirection, double workingSpace, double minValue, double maxValue, AxesChartStyler styler) {
        super(axisDirection, workingSpace, minValue, maxValue, styler);
        numberFormatter = new NumberFormatter(styler, axisDirection, minValue, maxValue);
        axisFormat = numberFormatter;
        calculate();
    }

    private void calculate() {
        if (minValue == maxValue) {
            tickLabels.add(numberFormatter.format(BigDecimal.valueOf(maxValue)));
            tickLocations.add(workingSpace / 2.0);
            return;
        }
        double tickSpace = styler.getPlotContentSize() * workingSpace;
        if (tickSpace < styler.getXAxisTickMarkSpacingHint()) {
            return;
        }
        double margin = (workingSpace - tickSpace) / 2.0;
        double span = Math.abs(Math.min((maxValue - minValue), Double.MAX_VALUE - 1));
        int tickSpacingHint = (axisDirection == Direction.X ? styler.getXAxisTickMarkSpacingHint() : styler.getYAxisTickMarkSpacingHint()) - 5;
        if (axisDirection == Direction.Y && tickSpace < 160) {
            tickSpacingHint = 25 - 5;
        }
        int gridStepInChartSpace;
        do {
            tickLabels.clear();
            tickLocations.clear();
            tickSpacingHint += 5;
            double significand = span / tickSpace * tickSpacingHint;
            int exponent = 0;
            if (significand == 0) {
                exponent = 1;
            } else if (significand < 1) {
                while (significand < 1) {
                    significand *= 10.0;
                    exponent--;
                }
            } else {
                while (significand >= 10 || significand == Double.NEGATIVE_INFINITY) {
                    significand /= 10.0;
                    exponent++;
                }
            }
            double gridStep;
            if (significand > 7.5) {
                gridStep = 10.0 * pow(exponent);
            } else if (significand > 3.5) {
                gridStep = 5.0 * pow(exponent);
            } else if (significand > 1.5) {
                gridStep = 2.0 * pow(exponent);
            } else {
                gridStep = pow(exponent);
            }
            gridStepInChartSpace = (int) (gridStep / span * tickSpace);
            BigDecimal gridStepBigDecimal = BigDecimal.valueOf(gridStep);
            BigDecimal cleanedGridStep = gridStepBigDecimal.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
            BigDecimal firstPosition = BigDecimal.valueOf(getFirstPosition(cleanedGridStep.doubleValue()));
            BigDecimal cleanedFirstPosition = firstPosition.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
            for (BigDecimal value = cleanedFirstPosition;
                 value.compareTo(BigDecimal.valueOf(maxValue + 2 * cleanedGridStep.doubleValue())) < 0;
                 value = value.add(cleanedGridStep)) {
                String tickLabel = numberFormatter.format(value);
                tickLabels.add(tickLabel);
                double tickLabelPosition = margin + ((value.doubleValue() - minValue) / (maxValue - minValue) * tickSpace);
                tickLocations.add(tickLabelPosition);
            }
        } while (!willLabelsFitInTickSpaceHint(tickLabels, gridStepInChartSpace));
    }

    private static double pow(int exponent) {
        return exponent > 0 ? Math.pow(10, exponent) : 1.0 / Math.pow(10, -1 * exponent);
    }
}
