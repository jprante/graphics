package org.xbib.graphics.pdfbox.print.test;

import javax.print.DocFlavor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.print.PrintUtility;
import org.xbib.graphics.pdfbox.print.Printer;

import java.util.logging.Level;
import java.util.logging.Logger;

@Disabled
public class PrinterTest {

    private static final Logger logger = Logger.getLogger(PrinterTest.class.getName());

    @Test
    public void testPrinterSelection() {
        logger.log(Level.INFO, "printer = " + PrintUtility.findPrinters(DocFlavor.INPUT_STREAM.AUTOSENSE));
    }

    @Test
    public void print() throws Exception {
        Printer printer = PrintUtility.getPrinter("Samsung ML-1610 (USB001)", DocFlavor.INPUT_STREAM.AUTOSENSE);
        if (printer != null) {
            PrintUtility.print(getClass().getResourceAsStream("/test.pdf"), printer);
        }
    }
}
