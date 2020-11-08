package org.xbib.graphics.io.pdfbox;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;

import java.awt.Color;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.IOException;
import java.io.OutputStream;

/*
  Usage:

  PdfBoxGraphics2D pdfBoxGraphics2D = new PdfBoxGraphics2D(this.doc, (int)(width), (int)(height));
  PdfBoxGraphics2DColorMapper colorMapper = new RGBtoCMYKColorMapper(icc_profile);
  pdfBoxGraphics2D.setColorMapper(colorMapper);

  Where icc_profile is an instance of java.awt.color.ICC_Profile that supports a CMYK
  colorspace. For testing purposes, we're using ISOcoated_v2_300_bas.icc which ships
  with PDFBox.
 */
public class RGBtoCMYKColorMapper extends DefaultColorMapper {

    private final ICC_ColorSpace icc_colorspace;

    private final PDICCBased pdProfile;

    public RGBtoCMYKColorMapper(ICC_Profile icc_profile, PDDocument document) throws IOException {
        icc_colorspace = new ICC_ColorSpace(icc_profile);
        this.pdProfile = new PDICCBased(document);
        OutputStream outputStream = pdProfile.getPDStream().createOutputStream(COSName.FLATE_DECODE);
        outputStream.write(icc_profile.getData());
        outputStream.close();
        pdProfile.getPDStream().getCOSObject().setInt(COSName.N, 4);
        pdProfile.getPDStream().getCOSObject().setItem(COSName.ALTERNATE, COSName.DEVICECMYK);
    }

    public PDColor mapColor(PDPageContentStream contentStream, Color rgbColor) {
        int r = rgbColor.getRed();
        int g = rgbColor.getGreen();
        int b = rgbColor.getBlue();
        int[] rgbInts = {r, g, b};
        float[] rgbFoats = rgbIntToFloat(rgbInts);
        float[] cmykFloats = icc_colorspace.fromRGB(rgbFoats);
        return new PDColor(cmykFloats, pdProfile);
    }

    public static float[] rgbIntToFloat(int[] rgbInts) {
        float red = (float) rgbInts[0] / 255.0f;
        float green = (float) rgbInts[1] / 255.0f;
        float blue = (float) rgbInts[2] / 255.0f;
        return new float[]{red, green, blue};
    }
}
