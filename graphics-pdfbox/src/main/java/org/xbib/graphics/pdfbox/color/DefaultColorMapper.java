package org.xbib.graphics.pdfbox.color;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceCMYK;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.xbib.graphics.pdfbox.paint.DefaultPaintApplier;

import java.awt.Color;

public class DefaultColorMapper implements ColorMapper {

    @Override
    public PDColor mapColor(PDPageContentStream contentStream, Color color) {
        if (color == null) {
            return new PDColor(new float[]{1f, 1f, 1f}, PDDeviceRGB.INSTANCE);
        }
        if (color.getClass().getSimpleName().equals("CMYKColor")) {
            float c = DefaultPaintApplier.getPropertyValue(color, "getC");
            float m = DefaultPaintApplier.getPropertyValue(color, "getM");
            float y = DefaultPaintApplier.getPropertyValue(color, "getY");
            float k = DefaultPaintApplier.getPropertyValue(color, "getK");
            return new PDColor(new float[]{c, m, y, k}, PDDeviceCMYK.INSTANCE);
        }
        if (color instanceof CMYKColor) {
            return ((CMYKColor) color).toPDColor();
        }
        float[] components = new float[]{color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f};
        return new PDColor(components, PDDeviceRGB.INSTANCE);
    }
}
