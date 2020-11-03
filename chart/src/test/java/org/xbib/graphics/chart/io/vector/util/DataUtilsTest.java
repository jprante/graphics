package org.xbib.graphics.chart.io.vector.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class DataUtilsTest {

    @Test
    public void stripTrailingSpaces() {
        String result = DataUtils.stripTrailing(" foo bar!   ", " ");
        String expected = " foo bar!";
        assertEquals(expected, result);
    }

    @Test
    public void stripTrailingSpacesInMultilineString() {
        String result = DataUtils.stripTrailing(" foo bar! \n   ", " ");
        String expected = " foo bar! \n";
        assertEquals(expected, result);
    }

    @Test
    public void stripComplexSubstring() {
        String result = DataUtils.stripTrailing("+bar foo+bar+bar+bar", "+bar");
        String expected = "+bar foo";
        assertEquals(expected, result);
    }
}
