package org.xbib.graphics.chart.io.vector.pdf;

import org.xbib.graphics.chart.io.vector.util.DataUtils;
import org.xbib.graphics.chart.io.vector.util.GraphicsUtils;

import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Resources extends PDFObject {

    private static final String KEY_PROC_SET = "ProcSet";
    private static final String KEY_TRANSPARENCY = "ExtGState";
    private static final String KEY_FONT = "Font";
    private static final String KEY_IMAGE = "XObject";

    private static final String[] VALUE_PROC_SET = {"PDF", "Text", "ImageB", "ImageC", "ImageI"};

    private static final String PREFIX_FONT = "Fnt";
    private static final String PREFIX_IMAGE = "Img";
    private static final String PREFIX_TRANSPARENCY = "Trp";

    private final Map<Font, String> fonts;
    private final Map<PDFObject, String> images;
    private final Map<Double, String> transparencies;

    private final AtomicInteger currentFontId = new AtomicInteger();
    private final AtomicInteger currentImageId = new AtomicInteger();
    private final AtomicInteger currentTransparencyId = new AtomicInteger();

    public Resources(int id, int version) {
        super(id, version, null, null);
        fonts = new HashMap<>();
        images = new HashMap<>();
        transparencies = new HashMap<>();
        dict.put(KEY_PROC_SET, VALUE_PROC_SET);
    }

    private <T> String getResourceId(Map<T, String> resources, T resource,
                                     String idPrefix, AtomicInteger idCounter) {
        String id = resources.get(resource);
        if (id == null) {
            id = String.format("%s%d", idPrefix, idCounter.getAndIncrement());
            resources.put(resource, id);
        }
        return id;
    }

    @SuppressWarnings("unchecked")
    public String getId(Font font) {
        Map<String, Map<String, Object>> dictEntry =
                (Map<String, Map<String, Object>>) dict.get(KEY_FONT);
        if (dictEntry == null) {
            dictEntry = new LinkedHashMap<>();
            dict.put(KEY_FONT, dictEntry);
        }
        font = GraphicsUtils.getPhysicalFont(font);
        String resourceId = getResourceId(fonts, font, PREFIX_FONT, currentFontId);
        String fontName = font.getPSName();
        // TODO: Determine font encoding (e.g. MacRomanEncoding, MacExpertEncoding, WinAnsiEncoding)
        String fontEncoding = "WinAnsiEncoding";
        dictEntry.put(resourceId, DataUtils.map(
                new String[]{"Type", "Subtype", "Encoding", "BaseFont"},
                new Object[]{"Font", "TrueType", fontEncoding, fontName}
        ));
        return resourceId;
    }

    @SuppressWarnings("unchecked")
    public String getId(PDFObject image) {
        Map<String, PDFObject> dictEntry = (Map<String, PDFObject>) dict.get(KEY_IMAGE);
        if (dictEntry == null) {
            dictEntry = new LinkedHashMap<>();
            dict.put(KEY_IMAGE, dictEntry);
        }
        String resourceId = getResourceId(images, image, PREFIX_IMAGE, currentImageId);
        dictEntry.put(resourceId, image);
        return resourceId;
    }

    @SuppressWarnings("unchecked")
    public String getId(Double transparency) {
        Map<String, Map<String, Object>> dictEntry =
                (Map<String, Map<String, Object>>) dict.get(KEY_TRANSPARENCY);
        if (dictEntry == null) {
            dictEntry = new LinkedHashMap<>();
            dict.put(KEY_TRANSPARENCY, dictEntry);
        }
        String resourceId = getResourceId(transparencies, transparency,
                PREFIX_TRANSPARENCY, currentTransparencyId);
        dictEntry.put(resourceId, DataUtils.map(
                new String[]{"Type", "ca", "CA"},
                new Object[]{"ExtGState", transparency, transparency}
        ));
        return resourceId;
    }
}
