package org.xbib.graphics.io.vector.pdf.util;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
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
        font = getPhysicalFont(font);
        String resourceId = getResourceId(fonts, font, PREFIX_FONT, currentFontId);
        String fontName = font.getPSName();
        String fontEncoding = "WinAnsiEncoding";
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Type", "Font");
        map.put("Subtype", "TrueType");
        map.put("Encoding", fontEncoding);
        map.put("BaseFont", fontName);
        dictEntry.put(resourceId,map);
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
        String resourceId = getResourceId(transparencies, transparency, PREFIX_TRANSPARENCY, currentTransparencyId);
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("Type", "ExtGState");
        map.put("ca", transparency);
        map.put( "CA", transparency);
        dictEntry.put(resourceId, map);
        return resourceId;
    }

    /**
     * Try to guess physical font from the properties of a logical font, like
     * "Dialog", "Serif", "Monospaced" etc.
     *
     * @param logicalFont Logical font object.
     * @param testText    Text used to determine font properties.
     * @return An object of the first matching physical font. The original font
     * object is returned if it was a physical font or no font matched.
     */
    private static Font getPhysicalFont(Font logicalFont, String testText) {
        String logicalFamily = logicalFont.getFamily();
        if (!isLogicalFontFamily(logicalFamily)) {
            return logicalFont;
        }
        final TextLayout logicalLayout = new TextLayout(testText, logicalFont, FONT_RENDER_CONTEXT);
        Queue<Font> physicalFonts = new PriorityQueue<>(1, FONT_EXPRESSIVENESS_COMPARATOR);
        Font[] allPhysicalFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
        for (Font physicalFont : allPhysicalFonts) {
            String physicalFamily = physicalFont.getFamily();
            if (isLogicalFontFamily(physicalFamily)) {
                continue;
            }
            physicalFont = physicalFont.deriveFont(logicalFont.getStyle(), logicalFont.getSize2D());
            TextLayout physicalLayout = new TextLayout(testText, physicalFont, FONT_RENDER_CONTEXT);
            if (physicalLayout.getBounds().equals(logicalLayout.getBounds()) &&
                    physicalLayout.getAscent() == logicalLayout.getAscent() &&
                    physicalLayout.getDescent() == logicalLayout.getDescent() &&
                    physicalLayout.getLeading() == logicalLayout.getLeading() &&
                    physicalLayout.getAdvance() == logicalLayout.getAdvance() &&
                    physicalLayout.getVisibleAdvance() == logicalLayout.getVisibleAdvance()) {
                physicalFonts.add(physicalFont);
            }
        }
        if (physicalFonts.isEmpty()) {
            return logicalFont;
        }
        return physicalFonts.poll();
    }

    private static Font getPhysicalFont(Font logicalFont) {
        return getPhysicalFont(logicalFont, FONT_TEST_STRING);
    }

    private static boolean isLogicalFontFamily(String family) {
        return (Font.DIALOG.equals(family) ||
                Font.DIALOG_INPUT.equals(family) ||
                Font.SANS_SERIF.equals(family) ||
                Font.SERIF.equals(family) ||
                Font.MONOSPACED.equals(family));
    }

    private static final FontRenderContext FONT_RENDER_CONTEXT =
            new FontRenderContext(null, false, true);

    private static final String FONT_TEST_STRING =
            "Falsches Üben von Xylophonmusik quält jeden größeren Zwerg";

    private static final FontExpressivenessComparator FONT_EXPRESSIVENESS_COMPARATOR =
            new FontExpressivenessComparator();

    private static class FontExpressivenessComparator implements Comparator<Font> {
        private static final int[] STYLES = {
                Font.PLAIN, Font.ITALIC, Font.BOLD, Font.BOLD | Font.ITALIC
        };

        @Override
        public int compare(Font font1, Font font2) {
            if (font1 == font2) {
                return 0;
            }
            Set<String> variantNames1 = new HashSet<>();
            Set<String> variantNames2 = new HashSet<>();
            for (int style : STYLES) {
                variantNames1.add(font1.deriveFont(style).getPSName());
                variantNames2.add(font2.deriveFont(style).getPSName());
            }
            if (variantNames1.size() < variantNames2.size()) {
                return 1;
            } else if (variantNames1.size() > variantNames2.size()) {
                return -1;
            }
            return font1.getName().compareTo(font2.getName());
        }
    }

}
