package org.xbib.graphics.printer.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.printer.PrintUtility;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrinterTest {

    private static final Logger logger = Logger.getLogger(PrinterTest.class.getName());

    @Test
    public void testPrinterSelection() {
        logger.log(Level.INFO, "printer = " + PrintUtility.findPrinters());
    }

}
