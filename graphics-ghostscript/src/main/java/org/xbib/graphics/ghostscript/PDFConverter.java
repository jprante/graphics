package org.xbib.graphics.ghostscript;

import org.xbib.graphics.ghostscript.internal.LoggingOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFConverter {

    private static final Logger logger = Logger.getLogger(PDFConverter.class.getName());

    public static final int OPTION_AUTOROTATEPAGES_NONE = 0;
    public static final int OPTION_AUTOROTATEPAGES_ALL = 1;
    public static final int OPTION_AUTOROTATEPAGES_PAGEBYPAGE = 2;
    public static final int OPTION_AUTOROTATEPAGES_OFF = 3;

    public static final int OPTION_PROCESSCOLORMODEL_RGB = 0;
    public static final int OPTION_PROCESSCOLORMODEL_GRAY = 1;
    public static final int OPTION_PROCESSCOLORMODEL_CMYK = 2;

    public static final int OPTION_PDFSETTINGS_DEFAULT = 0;
    public static final int OPTION_PDFSETTINGS_SCREEN = 1;
    public static final int OPTION_PDFSETTINGS_EBOOK = 2;
    public static final int OPTION_PDFSETTINGS_PRINTER = 3;
    public static final int OPTION_PDFSETTINGS_PREPRESS = 4;

    /**
     * Define auto rotate pages behaviour. Can be OPTION_AUTOROTATEPAGES_NONE,
     * OPTION_AUTOROTATEPAGES_ALL, OPTION_AUTOROTATEPAGES_PAGEBYPAGE or
     * OPTION_AUTOROTATEPAGES_OFF (default).
     */
    private final int autoRotatePages;

    /**
     * Define process color model. Can be OPTION_PROCESSCOLORMODEL_RGB,
     * OPTION_PROCESSCOLORMODEL_GRAY or OPTION_PROCESSCOLORMODEL_CMYK.
     */
    private final int processColorModel;

    /**
     * Define PDF settings to use. Can be OPTION_PDFSETTINGS_DEFAULT,
     * OPTION_PDFSETTINGS_SCREEN, OPTION_PDFSETTINGS_EBOOK,
     * OPTION_PDFSETTINGS_PRINTER or OPTION_PDFSETTINGS_PREPRESS.
     */
    private final int pdfsettings;

    /**
     * Define PDF version compatibility level (default is "1.4").
     */
    private final String compatibilityLevel;

    /**
     * Enable PDFX generation (default is false).
     */
    private final boolean pdfx;

    /**
     * Define standard paper size for the generated PDF file. This parameter is
     * ignored if a paper size is provided in the input file. Default value is
     * "A4".
     */
    private final PaperSize paperSize;

    private final Path tmpPath;

    public PDFConverter() {
        this(OPTION_AUTOROTATEPAGES_OFF, OPTION_PROCESSCOLORMODEL_RGB,
                OPTION_PDFSETTINGS_PRINTER, "1.4", false, PaperSize.A4);
    }

    public PDFConverter(int autoRotatePages,
                        int processColorModel,
                        int pdfsettings,
                        String compatibilityLevel,
                        boolean pdfx,
                        PaperSize paperSize) {
        this.autoRotatePages = autoRotatePages;
        this.processColorModel = processColorModel;
        this.pdfsettings = pdfsettings;
        this.compatibilityLevel = compatibilityLevel;
        this.pdfx = pdfx;
        this.paperSize = paperSize;
        this.tmpPath = Paths.get("/var/tmp/" + this);
    }

    /**
     * Run method called to perform the actual process of the converter.
     *
     * @param inputStream  the input document
     * @param outputStream output stream
     * @throws IOException if conversion fails
     */
    public synchronized void convert(InputStream inputStream, OutputStream outputStream)
            throws IOException {
        if (outputStream == null) {
            return;
        }
        prepare(tmpPath);
        Path output = Files.createTempFile(tmpPath, "pdf", "pdf");
        Ghostscript gs = Ghostscript.getInstance();
        List<String> gsArgs = new LinkedList<>();
        gsArgs.add("-ps2pdf");
        gsArgs.add("-dNOPAUSE");
        //gsArgs.add("-dQUIET");
        gsArgs.add("-dBATCH");
        gsArgs.add("-dSAFER");
        switch (autoRotatePages) {
            case OPTION_AUTOROTATEPAGES_NONE:
                gsArgs.add("-dAutoRotatePages=/None");
                break;
            case OPTION_AUTOROTATEPAGES_ALL:
                gsArgs.add("-dAutoRotatePages=/All");
                break;
            case OPTION_AUTOROTATEPAGES_PAGEBYPAGE:
                gsArgs.add("-dAutoRotatePages=/PageByPage");
                break;
            default:
                break;
        }
        switch (processColorModel) {
            case OPTION_PROCESSCOLORMODEL_CMYK:
                gsArgs.add("-dProcessColorModel=/DeviceCMYK");
                break;
            case OPTION_PROCESSCOLORMODEL_GRAY:
                gsArgs.add("-dProcessColorModel=/DeviceGray");
                break;
            default:
                gsArgs.add("-dProcessColorModel=/DeviceRGB");
                break;
        }
        switch (pdfsettings) {
            case OPTION_PDFSETTINGS_EBOOK:
                gsArgs.add("-dPDFSETTINGS=/ebook");
                break;
            case OPTION_PDFSETTINGS_SCREEN:
                gsArgs.add("-dPDFSETTINGS=/screen");
                break;
            case OPTION_PDFSETTINGS_PRINTER:
                gsArgs.add("-dPDFSETTINGS=/printer");
                break;
            case OPTION_PDFSETTINGS_PREPRESS:
                gsArgs.add("-dPDFSETTINGS=/prepress");
                break;
            default:
                gsArgs.add("-dPDFSETTINGS=/default");
                break;
        }
        gsArgs.add("-dCompatibilityLevel=" + compatibilityLevel);
        gsArgs.add("-dPDFX=" + pdfx);
        gsArgs.add("-dDEVICEWIDTHPOINTS=" + paperSize.getWidth());
        gsArgs.add("-dDEVICEHEIGHTPOINTS=" + paperSize.getHeight());
        gsArgs.add("-sDEVICE=pdfwrite");
        gsArgs.add("-sOutputFile=" + output.toAbsolutePath().toString());
        //gsArgs.add("-q");
        gsArgs.add("-f");
        gsArgs.add("-");
        try {
            gs.setStdIn(inputStream);
            gs.setStdOut(new LoggingOutputStream(logger));
            gs.setStdErr(new LoggingOutputStream(logger));
            gs.initialize(gsArgs.toArray(new String[gsArgs.size()]));
            Files.copy(output.toAbsolutePath(), outputStream);
        } finally {
            Ghostscript.deleteInstance();
            delete(tmpPath);
        }
    }

    private static void prepare(Path path) throws IOException {
        Files.createDirectories(path);
    }

    private static void delete(Path path) throws IOException {
        if (path == null) {
            return;
        }
        if (!Files.exists(path)) {
            return;
        }
        try {
            Files.walkFileTree(path.toAbsolutePath(), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            logger.log(Level.WARNING, e.getMessage(), e);
        } finally {
            Files.deleteIfExists(path);
        }
    }
}
