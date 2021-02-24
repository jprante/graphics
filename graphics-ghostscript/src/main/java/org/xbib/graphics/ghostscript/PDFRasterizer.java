package org.xbib.graphics.ghostscript;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.xbib.graphics.ghostscript.internal.LoggingOutputStream;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PDFRasterizer implements Closeable {

    private static final Logger logger = Logger.getLogger(PDFRasterizer.class.getName());

    private final String creator;

    private final String author;

    private final String subject;

    private final Map<String, ImageReader> imageReaders;

    public PDFRasterizer(String subject) {
        this("org.xbib.graphics.gs/1.0.8", "Jörg Prante <prante@hbz-nrw.de>", subject);
    }

    public PDFRasterizer(String creator, String author, String subject) {
        this.creator = creator;
        this.author = author;
        this.subject = subject;
        this.imageReaders = createImageReaders();
    }

    @Override
    public void close() throws IOException {
        disposeImageReaders(imageReaders);
    }

    public synchronized void convert(Path source, Path target) throws IOException {
        logger.info("convert source=" + source + " target=" + target);
        if (!Files.exists(source)) {
            throw new FileNotFoundException(source.toString());
        }
        if (!Files.isReadable(source)) {
            throw new IOException("unable to read " + source.toString());
        }
        if (Files.size(source) == 0) {
            throw new IOException("empty file at " + source.toString());
        }
        Path tmp = Files.createTempDirectory("pdf-rasterize");
        if (!Files.isWritable(tmp)) {
            throw new IOException("unable to write to " + tmp.toString());
        }
        try {
            pdfToImage(source, tmp, "pdf", null);
            Path tmpTarget = tmp.resolve(target.getFileName());
            mergeImagesToPDF(tmp, tmpTarget);
            scalePDF(tmpTarget, target);
            logger.info("convert source=" + source + " done");
        } finally {
            delete(tmp);
        }
    }

    public synchronized void screenConvert(Path source, Path target) throws IOException {
        logger.info("convert source=" + source.toAbsolutePath() + " target=" + target);
        if (!Files.exists(source.toAbsolutePath())) {
            throw new FileNotFoundException(source.toString());
        }
        if (!Files.isReadable(source.toAbsolutePath())) {
            throw new IOException("unable to read " + source.toString());
        }
        Path tmp = Files.createTempDirectory("pdf-rasterize");
        try {
            pdfToGrayScreenImage(source.toAbsolutePath(), tmp);
            Path tmpTarget = tmp.resolve(target.getFileName());
            mergeImagesToPDF(tmp, tmpTarget);
            //toPDFA(tmpTarget, target);
            scalePDF(tmpTarget, target);
        } finally {
            delete(tmp);
            logger.info("convert source=" + source.toAbsolutePath() + " done");
        }
    }

    public synchronized void pdfToGrayScreenImage(Path source, Path target) throws IOException {
        logger.info("pdfToImage source=" + source + " target=" + target);
        if (!Files.exists(source.toAbsolutePath())) {
            throw new FileNotFoundException(source.toString());
        }
        if (!Files.isReadable(source.toAbsolutePath())) {
            throw new IOException("unable to read " + source.toString());
        }
        try {
            Ghostscript gs = Ghostscript.getInstance();
            List<String> gsArgs = new LinkedList<>();
            gsArgs.add("-dNOPAUSE");
            gsArgs.add("-dBATCH");
            //gsArgs.add("-dQUIET");
            // how do we know we have a crop box or not?
            gsArgs.add("-dUseCropBox");
            gsArgs.add("-sDEVICE=pnggray");
            gsArgs.add("-r72");
            // max 9999 pages
            gsArgs.add("-sOutputFile=" + target.resolve("screen-gray-pdf-%05d.png"));
            gsArgs.add("-f");
            gsArgs.add(source.toString());
            logger.info("pdfToImage args=" + gsArgs);
            // reset stdin
            gs.setStdIn(null);
            gs.setStdOut(new LoggingOutputStream(logger));
            gs.setStdErr(new LoggingOutputStream(logger));
            gs.initialize(gsArgs.toArray(new String[gsArgs.size()]));
            gs.exit();
        } finally {
            Ghostscript.deleteInstance();
            logger.info("pdfToImage done");
        }
    }

    public synchronized void pdfToImage(Path sourceFile,
                                        Path targetDir,
                                        String prefix,
                                        String pageRange) throws IOException {
        logger.info("pdfToImage source=" + sourceFile + " target=" + targetDir + " started");
        try {
            Ghostscript gs = Ghostscript.getInstance();
            List<String> gsArgs = new LinkedList<>();
            gsArgs.add("-dNOPAUSE");
            gsArgs.add("-dBATCH");
            gsArgs.add("-dQUIET");
            // expensive but required for smoothness
            gsArgs.add("-dINTERPOLATE");
            // how do we know we have a crop box or not?
            gsArgs.add("-dUseCropBox");
            // page range, if not null
            if (pageRange != null) {
                gsArgs.add("-sPageList=" + pageRange);
            }
            gsArgs.add("-sDEVICE=png16m");
            gsArgs.add("-r300");
            // max 9999 pages
            gsArgs.add("-sOutputFile=" + targetDir.resolve(prefix + "-%05d.png"));
            gsArgs.add("-dNumRenderingThreads=" + Runtime.getRuntime().availableProcessors() / 2);
            gsArgs.add("-dMaxBitmap=100000000");
            gsArgs.add("-c");
            gsArgs.add("100000000 setvmthreshold");
            gsArgs.add("-f");
            gsArgs.add(sourceFile.toString());
            logger.info("pdfToImage args=" + gsArgs);
            // reset stdin
            gs.setStdIn(null);
            gs.setStdOut(new LoggingOutputStream(logger));
            gs.setStdErr(new LoggingOutputStream(logger));
            gs.initialize(gsArgs.toArray(new String[gsArgs.size()]));
            gs.exit();
        } finally {
            Ghostscript.deleteInstance();
            logger.info("pdfToImage done");
        }
    }

    public synchronized int mergeImagesToPDF(Path sourceDir, Path targetFile) throws IOException {
        logger.info("mergeImagesToPDF: source=" + sourceDir + " target=" + targetFile);
        int pagecount = 0;
        List<PDDocument> coverPageDocs = new ArrayList<>();
        try (Stream<Path> files = Files.list(sourceDir);
             PDDocument pdDocument = new PDDocument();
             OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(targetFile))) {
            List<Path> entries = files.sorted()
                    .filter(PDFRasterizer::checkForRealFile)
                    .collect(Collectors.toList());
            pdDocument.getDocumentInformation().setTitle(targetFile.getFileName().toString());
            pdDocument.getDocumentInformation().setCreationDate(Calendar.getInstance());
            pdDocument.getDocumentInformation().setCreator(creator);
            pdDocument.getDocumentInformation().setSubject(subject);
            pdDocument.getDocumentInformation().setAuthor(author);
            for (Path path : entries) {
                if (path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                    logger.info("found pdf " + path);
                    try (InputStream inputStream = Files.newInputStream(path)) {
                        PDDocument doc = PDDocument.load(inputStream);
                        for (int i = 0; i < doc.getNumberOfPages(); i++) {
                            PDPage page = doc.getPage(i);
                            PDPage newPage = pdDocument.importPage(page); // shallow copy :(
                            newPage.setResources(page.getResources());
                            newPage.setCropBox(page.getCropBox());
                            newPage.setMediaBox(page.getMediaBox());
                            newPage.setRotation(page.getRotation());
                            pagecount++;
                        }
                        coverPageDocs.add(doc);
                    }
                } else if (isImageSuffix(path)) {
                    logger.log(Level.FINE, "found image " + path);
                    long size = Files.size(path);
                    if (size > 128 * 1024 * 1024) {
                        logger.log(Level.WARNING, "skipping image because too large: " + path + " size = " + size);
                    } else {
                        BufferedImage bufferedImage = readImage(path);
                        if (bufferedImage != null) {
                            PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
                            pdDocument.addPage(page);
                            PDImageXObject pdImageXObject = LosslessFactory.createFromImage(pdDocument, bufferedImage);
                            if (pdImageXObject != null) {
                                // true = use FlateDecode to compress
                                PDPageContentStream pdPageContentStream =
                                        new PDPageContentStream(pdDocument, page, PDPageContentStream.AppendMode.APPEND, true);
                                pdPageContentStream.drawImage(pdImageXObject, 0, 0);
                                pdPageContentStream.close();
                                pagecount++;
                            } else {
                                logger.log(Level.WARNING, "unable to create PDImageXObject from buffered image from " + path);
                                throw new IOException("unable to create PDImageXObject from buffered image");
                            }
                            bufferedImage.flush();
                        } else {
                            logger.log(Level.WARNING, "unable to read into a buffered image frmo " + path);
                            throw new IOException("unable to read into a buffered image");
                        }
                    }
                }
            }
            pdDocument.save(outputStream);
            logger.info("mergeImagesToPDF: done, " + pagecount + " pages");
        } finally {
            for (PDDocument pd : coverPageDocs) {
                pd.close();
            }
        }
        return pagecount;
    }

    public synchronized void scalePDF(Path sourceFile,
                                      Path targetFile) throws IOException {
        logger.info("scalePDF: source = " + sourceFile + " target = " + targetFile + " starting");
        try {
            Ghostscript gs = Ghostscript.getInstance();
            List<String> gsArgs = new LinkedList<>();
            gsArgs.add("-dNOPAUSE");
            gsArgs.add("-dBATCH");
            gsArgs.add("-dQUIET");
            gsArgs.add("-dPDFSETTINGS=/printer");
            gsArgs.add("-dFIXEDMEDIA");
            gsArgs.add("-dPDFFitPage");
            gsArgs.add("-dAutoRotatePages=/PageByPage");
            gsArgs.add("-dCompatibilityLevel=1.4");
            gsArgs.add("-sDEVICE=pdfwrite");
            gsArgs.add("-sPAPERSIZE=a4");
            gsArgs.add("-sOutputFile=" + targetFile.toString());
            gsArgs.add(sourceFile.toString());
            gs.setStdIn(null);
            logger.info(gsArgs.toString());
            gs.initialize(gsArgs.toArray(new String[gsArgs.size()]));
            gs.exit();
        } finally {
            Ghostscript.deleteInstance();
            logger.info("scalePDF: source = " + sourceFile + " target = " + targetFile + " done");
        }
    }

    public void toPDFA(Path source, Path target) throws IOException {
        Path iccPath = Files.createTempFile("srgb", ".icc");
        Path pdfapsPathTmp = Files.createTempFile("PDFA_def", ".tmp");
        Path pdfapsPath = Files.createTempFile("PDFA_def", ".ps");
        try {
            Ghostscript gs = Ghostscript.getInstance();
            Files.copy(getClass().getResourceAsStream("/iccprofiles/srgb.icc"),
                    iccPath, StandardCopyOption.REPLACE_EXISTING);
            Files.copy(getClass().getResourceAsStream("/lib/PDFA_def.ps"),
                    pdfapsPathTmp, StandardCopyOption.REPLACE_EXISTING);
            copyAndReplace(pdfapsPathTmp, pdfapsPath,
                    "srgb.icc",
                    iccPath.toAbsolutePath().toString());
            List<String> gsArgs = new LinkedList<>();
            gsArgs.add("-E");
            gsArgs.add("-dNOPAUSE");
            gsArgs.add("-dBATCH");
            gsArgs.add("-dQUIET"); // do not print to stdout
            gsArgs.add("-dNOSAFER"); // do not use SAFER because we need access to PDFA_def.ps and srgb.icc
            gsArgs.add("-dPDFA=2"); // PDF/A-2b
            gsArgs.add("-sColorConversionStrategy=/sRGB");
            gsArgs.add("-sOutputICCProfile=" + iccPath.toAbsolutePath().toString());
            gsArgs.add("-sDEVICE=pdfwrite");
            gsArgs.add("-dPDFSETTINGS=/printer");
            gsArgs.add("-sPAPERSIZE=a4");
            gsArgs.add("-dPDFFitPage");
            gsArgs.add("-dAutoRotatePages=/PageByPage");
            gsArgs.add("-sOutputFile=" + target.toString());
            gsArgs.add(pdfapsPath.toAbsolutePath().toString());
            gsArgs.add(source.toString());
            gs.setStdIn(null);
            gs.initialize(gsArgs.toArray(new String[gsArgs.size()]));
            gs.exit();
        } finally {
            Ghostscript.deleteInstance();
            delete(pdfapsPathTmp);
            delete(pdfapsPath);
            delete(iccPath);
        }
    }

    private void copyAndReplace(Path source, Path target, String from, String to) throws IOException {
        try (BufferedReader br = Files.newBufferedReader(source); BufferedWriter bw = Files.newBufferedWriter(target)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(from, to);
                bw.write(line + "\n");
            }
        }
    }

    private static boolean checkForRealFile(Path p) {
        try {
            return Files.isReadable(p)  && Files.isRegularFile(p) && Files.size(p) > 0;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isImageSuffix(Path path) {
        String string = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return string.endsWith(".png") ||
                string.endsWith(".pnm") ||
                string.endsWith(".jpg") ||
                string.endsWith(".jpeg") ||
                string.endsWith(".tiff") ||
                string.endsWith(".tif");
    }

    private Map<String, ImageReader> createImageReaders() {
        Map<String, ImageReader> map = new LinkedHashMap<>();
        ImageReader pngReader = getImageReader("png");
        if (pngReader != null) {
            ImageReadParam param = pngReader.getDefaultReadParam();
            logger.log(Level.FINE, "PNG reader: " + pngReader.getClass().getName() + " param: " + param);
            map.put("png", pngReader);
        }
        ImageReader pnmReader = getImageReader("pnm");
        if (pnmReader != null) {
            ImageReadParam param = pnmReader.getDefaultReadParam();
            logger.log(Level.FINE, "PNM reader: " + pnmReader.getClass().getName() + " param: " + param);
            map.put("pnm", pnmReader);
        }
        ImageReader jpegReader = getImageReader("jpeg");
        if (jpegReader != null) {
            ImageReadParam param = jpegReader.getDefaultReadParam();
            logger.log(Level.FINE, "JPEG reader: " + jpegReader.getClass().getName() + " param: " + param);
            map.put("jpg", jpegReader);
            map.put("jpeg", jpegReader);
        }
        ImageReader tiffReader = getImageReader("tiff");
        if (tiffReader != null) {
            ImageReadParam param = tiffReader.getDefaultReadParam();
            logger.log(Level.FINE, "TIFF reader: " + tiffReader.getClass().getName() + " param: " + param);
            map.put("tif", tiffReader);
            map.put("tiff", tiffReader);
        }
        return map;
    }

    private ImageReader getImageReader(String formatName) {
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(formatName);
        if (readers.hasNext()) {
            return readers.next();
        }
        return null;
    }

    private void disposeImageReaders(Map<String, ImageReader> map) {
        if (map != null) {
            for (Map.Entry<String, ImageReader> entry : map.entrySet()) {
                try {
                    entry.getValue().dispose();
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    private BufferedImage readImage(Path path) throws IOException {
        String suffix = getSuffix(path.getFileName().toString().toLowerCase(Locale.ROOT));
        ImageInputStream imageInputStream = ImageIO.createImageInputStream(path.toFile());
        if (imageInputStream != null) {
            ImageReader imageReader = imageReaders.get(suffix);
            if (imageReader != null) {
                imageReader.setInput(imageInputStream);
                BufferedImage bufferedImage = imageReader.read(0);
                imageInputStream.close();
                return bufferedImage;
            } else {
                throw new IOException("no image reader found for " + suffix);
            }
        } else {
            throw new IOException("no image input stream possible for " + path);
        }
    }

    private static String getSuffix(String filename) {
        int pos = filename.lastIndexOf('.');
        return pos >= 0 ? filename.substring(pos + 1).toLowerCase(Locale.ROOT) : null;
    }

    private static void delete(Path path) throws IOException {
        if (path == null) {
            return;
        }
        if (!Files.exists(path)) {
            return;
        }
        logger.info("delete " + path);
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
