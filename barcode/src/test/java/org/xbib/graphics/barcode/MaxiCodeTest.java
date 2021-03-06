package org.xbib.graphics.barcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * {@link MaxiCode} tests that can't be run via the {@link SymbolTest}.
 */
public class MaxiCodeTest {

    @Test
    public void testHumanReadableHeight() {
        MaxiCode maxicode = new MaxiCode();
        maxicode.setMode(4);
        maxicode.setContent("ABC");
        assertEquals(0, maxicode.getHumanReadableHeight());
    }

}
