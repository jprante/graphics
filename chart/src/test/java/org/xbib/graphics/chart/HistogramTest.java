package org.xbib.graphics.chart;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.jupiter.api.Test;

public class HistogramTest {

    @Test
    public void test1() {
        Histogram histogram = new Histogram(Arrays.asList(1, 2, 3, 4, 5, 6), 2, 0, 4);
        assertThat(histogram.getMax(), equalTo(4.0));
        assertThat(histogram.getMin(), equalTo(0.0));
        assertThat(histogram.getNumBins(), equalTo(2));
        assertThat(histogram.getyAxisData().get(0) + histogram.getyAxisData().get(1), equalTo(4.0));
    }

    @Test
    public void testNegativeValues() {
        Histogram histogram = new Histogram(Arrays.asList(-1, -2, -3, -4, -5, -6), 3);
        assertThat(histogram.getMax(), equalTo(-1.0));
        assertThat(histogram.getMin(), equalTo(-6.0));
        assertThat(histogram.getNumBins(), equalTo(3));
    }

}
