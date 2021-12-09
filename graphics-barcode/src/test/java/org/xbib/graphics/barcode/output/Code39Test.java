package org.xbib.graphics.barcode.output;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.barcode.Code3Of9;
import org.xbib.graphics.barcode.HumanReadableLocation;
import org.xbib.graphics.barcode.render.BarcodeGraphicsRenderer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class Code39Test {

    @Test
    public void createBarcode1() throws IOException {
        Code3Of9 code3Of9 = new Code3Of9();
        code3Of9.setContent("20180123456");
        code3Of9.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
        double scalingFactor = 3.0d;
        int width = (int) (code3Of9.getWidth() * scalingFactor);
        int height = (int) (code3Of9.getHeight() * scalingFactor);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BarcodeGraphicsRenderer renderer = createRenderer(bufferedImage, scalingFactor, scalingFactor);
        renderer.render(code3Of9);
        renderer.close();
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/barcode1.png"));
        ImageIO.write(bufferedImage, "png", outputStream);
        outputStream.close();
    }

    @Test
    public void createBarcode2() throws IOException {
        Code3Of9 code3Of9 = new Code3Of9();
        code3Of9.setContent("20180123456");
        code3Of9.setHumanReadableLocation(HumanReadableLocation.BOTTOM);
        double scalingFactorX = 6.0d;
        double scalingFactorY = 3.0d;
        int width = (int) (code3Of9.getWidth() * scalingFactorX);
        int height = (int) (code3Of9.getHeight() * scalingFactorY);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BarcodeGraphicsRenderer renderer = createRenderer(bufferedImage, scalingFactorX, scalingFactorY);
        renderer.render(code3Of9);
        renderer.close();
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/barcode2.png"));
        ImageIO.write(bufferedImage, "png", outputStream);
        outputStream.close();
    }

    @Test
    public void createBarcode3() throws IOException {
        int width = 512;
        int height = 150;
        Code3Of9 code3Of9 = new Code3Of9();
        code3Of9.setContent("11111111111");
        double scalingFactor = 3.0d;
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        BarcodeGraphicsRenderer renderer = createRenderer(bufferedImage, scalingFactor, scalingFactor);
        renderer.render(code3Of9);
        renderer.close();
        OutputStream outputStream = Files.newOutputStream(Paths.get("build/barcode3.png"));
        ImageIO.write(bufferedImage, "png", outputStream);
        outputStream.close();
    }

    private BarcodeGraphicsRenderer createRenderer(BufferedImage bufferedImage, double scalingFactorX, double scalingFactorY) {
        Graphics2D g2d = bufferedImage.createGraphics();
        Rectangle rectangle = new Rectangle(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
        return new BarcodeGraphicsRenderer(g2d, rectangle, scalingFactorX, scalingFactorY,
                Color.WHITE, Color.BLACK, false, false);
    }
}
