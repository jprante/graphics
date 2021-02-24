package org.xbib.graphics.chart.axis;

import org.xbib.graphics.chart.style.AxesChartStyler;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class encapsulates the logic to generate the axis tick mark and axis tick label data for rendering the axis
 * ticks for date axes
 */
public class AxisTickCalculatorInstant extends AxisTickCalculator {

    private static final long MILLIS_SCALE = TimeUnit.MILLISECONDS.toMillis(1L);
    private static final long SEC_SCALE = TimeUnit.SECONDS.toMillis(1L);
    private static final long MIN_SCALE = TimeUnit.MINUTES.toMillis(1L);
    private static final long HOUR_SCALE = TimeUnit.HOURS.toMillis(1L);
    private static final long DAY_SCALE = TimeUnit.DAYS.toMillis(1L);
    private static final long MONTH_SCALE = TimeUnit.DAYS.toMillis(1L) * 30;
    private static final long YEAR_SCALE = TimeUnit.DAYS.toMillis(1L) * 365;

    private static final List<TimeSpan> timeSpans = new ArrayList<>();

    static {
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 1, "ss.SSS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 2, "ss.SSS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 5, "ss.SSS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 10, "ss.SSS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 50, "ss.SS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 100, "ss.SS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 200, "ss.SS"));
        timeSpans.add(new TimeSpan(MILLIS_SCALE, 500, "ss.SS"));

        timeSpans.add(new TimeSpan(SEC_SCALE, 1, "ss.SS"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 2, "ss.S"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 5, "ss.S"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 10, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 15, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 20, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(SEC_SCALE, 30, "HH:mm:ss"));

        timeSpans.add(new TimeSpan(MIN_SCALE, 1, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 2, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 5, "HH:mm:ss"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 10, "HH:mm"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 15, "HH:mm"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 20, "HH:mm"));
        timeSpans.add(new TimeSpan(MIN_SCALE, 30, "HH:mm"));

        timeSpans.add(new TimeSpan(HOUR_SCALE, 1, "HH:mm"));
        timeSpans.add(new TimeSpan(HOUR_SCALE, 2, "HH:mm"));
        timeSpans.add(new TimeSpan(HOUR_SCALE, 4, "HH:mm"));
        timeSpans.add(new TimeSpan(HOUR_SCALE, 8, "HH:mm"));
        timeSpans.add(new TimeSpan(HOUR_SCALE, 12, "HH:mm"));

        timeSpans.add(new TimeSpan(DAY_SCALE, 1, "EEE HH:mm"));
        timeSpans.add(new TimeSpan(DAY_SCALE, 2, "EEE HH:mm"));
        timeSpans.add(new TimeSpan(DAY_SCALE, 3, "EEE HH:mm"));
        timeSpans.add(new TimeSpan(DAY_SCALE, 5, "MM-dd"));
        timeSpans.add(new TimeSpan(DAY_SCALE, 10, "MM-dd"));
        timeSpans.add(new TimeSpan(DAY_SCALE, 15, "MM-dd"));

        timeSpans.add(new TimeSpan(MONTH_SCALE, 1, "MM-dd"));
        timeSpans.add(new TimeSpan(MONTH_SCALE, 2, "MM-dd"));
        timeSpans.add(new TimeSpan(MONTH_SCALE, 3, "MM-dd"));
        timeSpans.add(new TimeSpan(MONTH_SCALE, 4, "MM-dd"));
        timeSpans.add(new TimeSpan(MONTH_SCALE, 6, "yyyy-MM"));

        timeSpans.add(new TimeSpan(YEAR_SCALE, 1, "yyyy-MM"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 2, "yyyy-MM"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 5, "yyyy"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 10, "yyyy"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 20, "yyyy"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 100, "yyyy"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 500, "yyyy"));
        timeSpans.add(new TimeSpan(YEAR_SCALE, 1000, "yyyy"));
    }

    public AxisTickCalculatorInstant(Direction axisDirection, double workingSpace, double minValue, double maxValue, AxesChartStyler styler) {
        super(axisDirection, workingSpace, minValue, maxValue, styler);
        calculate();
    }

    private void calculate() {
        double tickSpace = styler.getPlotContentSize() * workingSpace;
        if (tickSpace < styler.getXAxisTickMarkSpacingHint()) {
            return;
        }
        double margin = (workingSpace - tickSpace) / 2.0;
        long span = (long) Math.abs(maxValue - minValue);
        int tickSpacingHint = styler.getXAxisTickMarkSpacingHint();
        int gridStepInChartSpace;
        long gridStepHint = (long) (span / tickSpace * tickSpacingHint);
        int index = 0;
        for (int i = 0; i < timeSpans.size() - 1; i++) {
            if (span < ((timeSpans.get(i).getUnitAmount() * timeSpans.get(i).getMagnitude() + timeSpans.get(i + 1).getUnitAmount() * timeSpans.get(i + 1).getMagnitude()) / 2.0)) {
                index = i;
                break;
            }
        }
        String datePattern = timeSpans.get(index).getDatePattern();
        for (int i = index - 1; i > 0; i--) {
            if (gridStepHint > timeSpans.get(i).getUnitAmount() * timeSpans.get(i).getMagnitude()) {
                index = i;
                break;
            }
        }
        index--;
        do {
            tickLabels.clear();
            tickLocations.clear();
            double gridStep = timeSpans.get(++index).getUnitAmount() * timeSpans.get(index).getMagnitude(); // in time units (ms)
            gridStepInChartSpace = (int) (gridStep / span * tickSpace);
            double firstPosition = getFirstPosition(gridStep);
            if (styler.getDatePattern() != null) {
                datePattern = styler.getDatePattern();
            }
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern)
                    .withZone(styler.getZoneId());
            for (double value = firstPosition; value <= maxValue + 2 * gridStep; value = value + gridStep) {
                long l = Math.round(value);
                tickLabels.add(dateTimeFormatter.format(Instant.ofEpochMilli(l)));
                double tickLabelPosition = margin + ((value - minValue) / (maxValue - minValue) * tickSpace);
                tickLocations.add(tickLabelPosition);
            }
        } while (!willLabelsFitInTickSpaceHint(tickLabels, gridStepInChartSpace));
    }

    static class TimeSpan {

        private final long unitAmount;
        private final int magnitude;
        private final String datePattern;

        TimeSpan(long unitAmount, int magnitude, String datePattern) {
            this.unitAmount = unitAmount;
            this.magnitude = magnitude;
            this.datePattern = datePattern;
        }

        long getUnitAmount() {
            return unitAmount;
        }

        int getMagnitude() {
            return magnitude;
        }

        String getDatePattern() {
            return datePattern;
        }

        @Override
        public String toString() {
            return "TimeSpan [unitAmount=" + unitAmount + ", magnitude=" + magnitude + ", datePattern=" + datePattern + "]";
        }
    }
}
