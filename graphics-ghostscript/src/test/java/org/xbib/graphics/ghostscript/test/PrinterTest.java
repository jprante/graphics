package org.xbib.graphics.ghostscript.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.PrintUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrinterTest {

    private static final Logger logger = Logger.getLogger(PrinterTest.class.getName());

    @Test
    public void testPrinterSelection() {
        logger.log(Level.INFO, "printer = " + PrintUtility.findPrinters());
    }

}
