package org.xbib.graphics.pdfbox.print.test;

import javax.print.DocFlavor;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.pdfbox.print.PrintUtility;
import org.xbib.graphics.pdfbox.print.Printer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class PrinterTest {

    private static final Logger logger = Logger.getLogger(PrinterTest.class.getName());

    @Test
    public void testPrinterSelection() {
        logger.log(Level.INFO, "printer = " + PrintUtility.findPrinters(DocFlavor.INPUT_STREAM.PDF));
    }

    @Test
    public void print() throws Exception {
        Printer printer = PrintUtility.getPrinter("Samsung_ML-1610", DocFlavor.INPUT_STREAM.PDF);
        if (printer != null) {
            PrintUtility.print(getClass().getResourceAsStream("/test.pdf"), DocFlavor.INPUT_STREAM.PDF, printer);
        }
    }

}
