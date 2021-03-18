package org.xbib.graphics.pdfbox.print;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrinterInfo;
import javax.print.attribute.standard.PrinterMakeAndModel;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;

import java.awt.print.PrinterJob;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PrintUtility {

    public static void print(InputStream inputStream, DocFlavor docFlavor)
            throws Exception {
        print(inputStream, docFlavor, findDefaultPrinter(docFlavor));
    }

    public static void print(InputStream inputStream, DocFlavor docFlavor, Printer printer)
            throws Exception {
        if (inputStream == null || printer == null) {
            return;
        }
        PrintRequestAttributeSet pas = new HashPrintRequestAttributeSet();
        if (printer.isCopiesSupported()) {
            pas.add(new Copies(printer.getCopies()));
        }
        if (printer.isCollateSupported()) {
            pas.add(printer.isCollate() ? SheetCollate.COLLATED : SheetCollate.UNCOLLATED);
        }
        if (printer.isDuplexSupported()) {
            switch (printer.getDuplex()) {
                case Printer.DUPLEX_SIMPLEX:
                    pas.add(Sides.ONE_SIDED);
                    break;
                case Printer.DUPLEX_HORIZONTAL:
                    pas.add(Sides.DUPLEX);
                    break;
                case Printer.DUPLEX_VERTICAL:
                    pas.add(Sides.TUMBLE);
                    break;
                default:
                    break;
            }
        }
        if (printer.isModeSupported()) {
            switch (printer.getMode()) {
                case Printer.MODE_MONOCHROME:
                    pas.add(Chromaticity.MONOCHROME);
                    break;
                case Printer.MODE_COLOR:
                    pas.add(Chromaticity.COLOR);
                    break;
                default:
                    break;
            }
        }
        if (printer.isMediaSupported() && printer.getMediaSize() != null) {
            MediaSizeName mediaSizeName = (MediaSizeName) printer
                    .getMediaSizeNames().get(printer.getMediaSize());
            if (mediaSizeName != null)
                pas.add(mediaSizeName);
        }
        PrintService service = printer.getService();
        DocPrintJob job = service.createPrintJob();
        Doc doc = new SimpleDoc(inputStream, docFlavor, null);
        job.print(doc, pas);
    }

    public static void print(InputStream inputStream, Printer printer) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPageable(new PDFPageable(document));
        job.setPrintService(printer.getService());
        job.print();
    }

    public static Printer getPrinter(String printerName, DocFlavor docFlavor) {
        Printer printer = null;
        if (printerName != null) {
            List<Printer> printers = findPrinters(docFlavor);
            for (Printer p : printers) {
                if (printerName.equalsIgnoreCase(p.getName())) {
                    printer = p;
                    break;
                }
            }
        }
        if (printer == null) {
            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            printer = createPrinter(service);
        }
        /*if (printer != null) {
            printer.setCopies(1);
            printer.setCollate(false);
            printer.setDuplex(Printer.DUPLEX_SIMPLEX);
            printer.setMode(Printer.STATUS_ACCEPTING_JOBS);
            printer.setMediaSize("A4");
        }*/
        return printer;
    }

    public static Printer findDefaultPrinter(DocFlavor docFlavor) {
        List<Printer> printers = findPrinters(docFlavor);
        return printers.isEmpty() ? null : printers.get(0);
    }

    public static List<Printer> findPrinters(DocFlavor docFlavor) {
        List<Printer> printers = new ArrayList<>();
        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(docFlavor, printRequestAttributeSet);
        if (printServices != null) {
            for (PrintService service : printServices) {
                printers.add(createPrinter(service));
            }
        }
        return printers;
    }

    private static Printer createPrinter(PrintService service) {
        if (service == null) {
            return null;
        }
        Printer printer = new Printer();
        printer.setName(service.getName());
        PrintServiceAttribute attr = service.getAttribute(PrinterMakeAndModel.class);
        if (attr != null) {
            printer.setModel(attr.toString());
        }
        //attr = service.getAttribute( PrinterIsAcceptingJobs.class );
        printer.setStatus(Printer.STATUS_ACCEPTING_JOBS);
        attr = service.getAttribute(PrinterInfo.class);
        if (attr != null) {
            printer.setInfo(attr.toString());
        }
        printer.setCopiesSupported(service.isAttributeCategorySupported(Copies.class));
        int copies = 0;
        Copies copiesObj = (Copies) service.getDefaultAttributeValue(Copies.class);
        if (copiesObj != null) {
            copies = Integer.parseInt(copiesObj.toString());
        }
        if (copies <= 0) {
            copies = 1;
        }
        printer.setCopies(copies);
        boolean collateSupported = service.isAttributeCategorySupported(SheetCollate.class);
        printer.setCollateSupported(collateSupported);
        if (collateSupported) {
            SheetCollate collate = (SheetCollate) service.getDefaultAttributeValue(SheetCollate.class);
            if (collate == null) {
                collate = SheetCollate.UNCOLLATED;
            }
            printer.setCollate(collate == SheetCollate.COLLATED);
        }
        boolean modeSupported = service.isAttributeCategorySupported(Chromaticity.class);
        printer.setModeSupported(modeSupported);
        if (modeSupported) {
            Chromaticity chromaticity = (Chromaticity) service.getDefaultAttributeValue(Chromaticity.class);
            if (chromaticity == null) {
                chromaticity = Chromaticity.MONOCHROME;
            }
            if (chromaticity == Chromaticity.MONOCHROME) {
                printer.setMode(Printer.MODE_MONOCHROME);
            } else {
                printer.setMode(Printer.MODE_COLOR);
            }
        }
        boolean duplexSupported = service.isAttributeCategorySupported(Sides.class);
        printer.setDuplexSupported(duplexSupported);
        if (duplexSupported) {
            Sides sides = (Sides) service.getDefaultAttributeValue(Sides.class);
            if (sides == null) {
                sides = Sides.ONE_SIDED;
            }
            if (sides == Sides.ONE_SIDED) {
                printer.setDuplex(Printer.DUPLEX_SIMPLEX);
            }
            else if (sides == Sides.TUMBLE) {
                printer.setDuplex(Printer.DUPLEX_VERTICAL);
            } else {
                printer.setDuplex(Printer.DUPLEX_HORIZONTAL);
            }
        }
        boolean mediaSupported = service.isAttributeCategorySupported(Media.class);
        printer.setMediaSupported(mediaSupported);
        if (mediaSupported) {
            Object obj = service.getSupportedAttributeValues(Media.class, null, null);
            if (obj instanceof Media[]) {
                Media[] medias = (Media[]) obj;
                for (Media media : medias) {
                    if (media instanceof MediaSizeName) {
                        printer.addMediaSizeName(media.toString(), media);
                    }
                }
            }
            Media media = (Media) service.getDefaultAttributeValue(Media.class);
            if (media != null) {
                if (media instanceof MediaSizeName) {
                    printer.setMediaSize(media.toString());
                }
            }
        }
        printer.setService(service);
        return printer;
    }
}
