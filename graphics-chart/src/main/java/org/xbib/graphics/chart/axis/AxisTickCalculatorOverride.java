package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.formatter.DateFormatter;
import org.xbib.graphics.chart.formatter.NumberFormatter;
import org.xbib.graphics.chart.formatter.StringFormatter;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.util.Map;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for
 * rendering the axis ticks for given values and labels.
 */
class AxisTickCalculatorOverride extends AxisTickCalculator {

    public AxisTickCalculatorOverride(Direction axisDirection,
            double workingSpace,
            double minValue,
            double maxValue,
            AxesChartStyler styler,
            Map<Double, Object> labelOverrideMap) {
        super(axisDirection, workingSpace, minValue, maxValue, styler);
        axisFormat = new NumberFormatter(styler, axisDirection, minValue, maxValue);
        calculate(labelOverrideMap);
    }

    public AxisTickCalculatorOverride(Direction axisDirection,
            double workingSpace,
            AxesChartStyler styler,
            Map<Double, Object> markMap,
            DataType axisType,
            int categoryCount) {
        super(axisDirection, workingSpace, Double.NaN, Double.NaN, styler);
        if (axisType == DataType.String) {
            axisFormat = new StringFormatter();
        } else if (axisType == DataType.Number) {
            axisFormat = new NumberFormatter(styler, axisDirection, minValue, maxValue);
        } else if (axisType == DataType.Instant) {
            axisFormat = new DateFormatter(styler.getDatePattern(), styler.getLocale());
        }
        calculateForCategory(markMap, categoryCount);
    }

    private void calculate(Map<Double, Object> labelOverrideMap) {
        if (minValue == maxValue) {
            String label = labelOverrideMap.isEmpty() ? " " : labelOverrideMap.values().iterator().next().toString();
            tickLabels.add(label);
            tickLocations.add(workingSpace / 2.0);
            return;
        }
        double tickSpace = styler.getPlotContentSize() * workingSpace;
        if (tickSpace < styler.getXAxisTickMarkSpacingHint()) {
            return;
        }
        double margin = (workingSpace - tickSpace) / 2.0;
        for (Map.Entry<Double, Object> entry : labelOverrideMap.entrySet()) {
            Object value = entry.getValue();
            String tickLabel = value == null ? " " : value.toString();
            tickLabels.add(tickLabel);
            double tickLabelPosition =
                    margin + ((entry.getKey() - minValue) / (maxValue - minValue) * tickSpace);
            tickLocations.add(tickLabelPosition);
        }
    }

    private void calculateForCategory(Map<Double, Object> locationLabelMap, int categoryCount) {
        double tickSpace = styler.getPlotContentSize() * workingSpace;
        double margin = (workingSpace - tickSpace) / 2.0;
        double gridStep = (tickSpace / categoryCount);
        double firstPosition = gridStep / 2.0;
        for (Map.Entry<Double, Object> entry : locationLabelMap.entrySet()) {
            Object value = entry.getValue();
            String tickLabel = value == null ? " " : value.toString();
            tickLabels.add(tickLabel);
            double tickLabelPosition = margin + firstPosition + gridStep * entry.getKey();
            tickLocations.add(tickLabelPosition);
        }
    }
}
