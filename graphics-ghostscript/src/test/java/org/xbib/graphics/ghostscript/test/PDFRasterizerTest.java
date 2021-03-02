package org.xbib.graphics.ghostscript.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.PDFRasterizer;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class PDFRasterizerTest {

    private static final Logger logger = Logger.getLogger(PDFRasterizerTest.class.getName());

    @Test
    public void testPDFCreation() throws IOException {
        String creator = "Xbib PDF";
        String author = "Jörg Prante";
        String subject = "Test";
        Path sourceDir = Paths.get("src/test/resources/org/xbib/graphics/ghostscript/test/images-3656573");
        Path targetFile = Paths.get("build/3656573.pdf");
        PDFRasterizer pdfRasterizer = new PDFRasterizer(creator, author, subject);
        int pagecount = pdfRasterizer.mergeImagesToPDF(sourceDir, targetFile);
        logger.info("pagecount = " + pagecount);
        pdfRasterizer.close();
    }

    @Test
    public void testPDFColorImage() throws IOException {
        String creator = "Xbib PDF";
        String author = "Jörg Prante";
        String subject = "Test";
        Path sourceDir = Paths.get("src/test/resources/org/xbib/graphics/ghostscript/test/images");
        Path targetFile = Paths.get("build/color.pdf");
        PDFRasterizer pdfRasterizer = new PDFRasterizer(creator, author, subject);
        int pagecount = pdfRasterizer.mergeImagesToPDF(sourceDir, targetFile);
        logger.info("pagecount = " + pagecount);
        pdfRasterizer.close();
    }

    @Test
    public void testPDFUnpackRasterAndScale() throws IOException {
        Path source = Paths.get("src/test/resources/org/xbib/graphics/ghostscript/test/20200024360.pdf");
        Path target = Paths.get("build/20200024360-new.pdf");
        Path tmp = Files.createTempDirectory("graphics-test");
        try {
            PDFRasterizer pdfRasterizer = new PDFRasterizer("xbib", "Jörg Prante", "Test");
            pdfRasterizer.pdfToImage(source, tmp, null, null);
            Path tmpTarget = tmp.resolve(target.getFileName());
            int pagecount = pdfRasterizer.mergeImagesToPDF(tmp, tmpTarget);
            logger.info("pagecount = " + pagecount);
            pdfRasterizer.scalePDF(tmpTarget, target);
            pdfRasterizer.close();
        } finally {
            delete(tmp);
        }
    }

    @Test
    public void testPDFRasterizerToImage() throws Exception {
        Path path = Paths.get("build/resources/test");
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
                if (p.toString().endsWith("cjk.pdf")) {
                    Path target = Paths.get("build/image-" + p.getFileName());
                    try {
                        delete(target);
                        Files.createDirectories(target);
                        PDFRasterizer rasterizer = new PDFRasterizer(
                                "org.xbib.graphics.ghostscript", "Jörg Prante",
                                "converted from " + p);
                        rasterizer.pdfToImage(p, target, "pdf-", "1-");
                        rasterizer.close();
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                        fail(e);
                    }
                }
            });
        }
    }

    @Test
    public void testPDFRasterizerConvert() throws Exception {
        logger.log(Level.INFO, "testing java.io.tmpdir = " + System.getProperty("java.io.tmpdir"));
        Path path = Paths.get("build/resources/test");
        try (Stream<Path> stream = Files.list(path)) {
            stream.forEach(p -> {
               if (p.toString().endsWith(".pdf")) {
                   logger.info("found " + p.toString());
                   Path target = Paths.get("build/" + p.getFileName());
                   try {
                       delete(target);
                       Files.createDirectories(target.getParent());
                       PDFRasterizer rasterizer = new PDFRasterizer(
                           "org.xbib.graphics.ghostscript", "Jörg Prante",
                           "converted from " + p);
                       rasterizer.convert(p, target);
                       rasterizer.close();
                   } catch (IOException e) {
                       logger.log(Level.SEVERE, e.getMessage(), e);
                       fail(e);
                   }
               }
            });
        }
    }

    private static void delete(Path path) throws IOException {
        if (path == null) {
            return;
        }
        if (!Files.exists(path)) {
            return;
        }
        try {
            // delete sub trees
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
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
        } finally {
            // and finally, delete the path
            Files.deleteIfExists(path);
        }
    }
}
