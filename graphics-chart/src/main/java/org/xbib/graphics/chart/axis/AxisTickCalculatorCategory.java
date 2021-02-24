package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.formatter.DateFormatter;
import org.xbib.graphics.chart.formatter.NumberFormatter;
import org.xbib.graphics.chart.formatter.StringFormatter;
import org.xbib.graphics.chart.style.AxesChartStyler;

import java.math.BigDecimal;
import java.util.List;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis
 * ticks for String axes
 */
public class AxisTickCalculatorCategory extends AxisTickCalculator {

    public AxisTickCalculatorCategory(Direction axisDirection, double workingSpace, List<?> categories, DataType axisType, AxesChartStyler styler) {
        super(axisDirection, workingSpace, Double.NaN, Double.NaN, styler);
        calculate(categories, axisType);
    }

    private void calculate(List<?> categories, DataType axisType) {
        // tick space - a percentage of the working space available for ticks
        double tickSpace = styler.getPlotContentSize() * workingSpace; // in plot space
        // where the tick should begin in the working space in pixels
        double margin = (workingSpace - tickSpace) / 2.0;
        // generate all tickLabels and tickLocations from the first to last position
        double gridStep = (tickSpace / (double) categories.size());
        double firstPosition = gridStep / 2.0;
        // set up String formatters that may be encountered
        if (axisType == DataType.String) {
            axisFormat = new StringFormatter();
        } else if (axisType == DataType.Number) {
            axisFormat = new NumberFormatter(styler, axisDirection, minValue, maxValue);
        } else if (axisType == DataType.Instant) {
            axisFormat = new DateFormatter(styler.getDatePattern(), styler.getLocale());
        }
        int counter = 0;
        for (Object category : categories) {
            if (axisType == DataType.String) {
                tickLabels.add(category.toString());
            } else if (axisType == DataType.Number) {
                tickLabels.add(axisFormat.format(new BigDecimal(category.toString()).doubleValue()));
            } else if (axisType == DataType.Instant) {
                tickLabels.add(axisFormat.format(category));
            }
            double tickLabelPosition = (int) (margin + firstPosition + gridStep * counter++);
            tickLocations.add(tickLabelPosition);
        }
    }
}
