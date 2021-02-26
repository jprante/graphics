package org.xbib.graphics.barcode;

import org.xbib.graphics.barcode.util.Hexagon;
import org.xbib.graphics.barcode.util.TextBox;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

public interface Symbol {

    void setDataType(SymbolDataType symbolDataType);

    void setBarHeight(int barHeight);

    int getBarHeight();

    void setModuleWidth(int moduleWidth);

    int getModuleWidth();

    void setFontName(String fontName);

    String getFontName();

    void setFontSize(double fontSize);

    double getFontSize();

    void setContent(String inputData);

    String getContent();

    void setHumanReadableLocation(HumanReadableLocation humanReadableLocation);

    HumanReadableLocation getHumanReadableLocation();

    int getHumanReadableHeight();

    void setQuietZoneVertical(int quietZoneVertical);

    int getQuietZoneVertical();

    void setQuietZoneHorizontal(int quietZoneHorizontal);

    int getQuietZoneHorizontal();

    int getHeight();

    int getWidth();

    boolean encode();

    void plotSymbol();

    List<Rectangle2D.Double> getRectangles();

    List<TextBox> getTexts();

    List<Hexagon> getHexagons();

    List<Ellipse2D.Double> getTarget();

}
