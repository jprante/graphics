package org.xbib.graphics.io.pdfbox.font;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.io.pdfbox.PdfBoxGraphics2D;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Paint;
import java.awt.font.TextAttribute;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default implementation to draw fonts. You can reuse instances of this class
 * within a PDDocument for more then one {@link PdfBoxGraphics2D}.
 * Just ensure that you call close after you closed the PDDocument to free any
 * temporary files.
 */
public class DefaultFontDrawer implements FontDrawer, Closeable {

    private static final Logger logger = Logger.getLogger(DefaultFontDrawer.class.getName());

    @Override
    public void close() {
        for (File tempFile : tempFiles) {
            if (!tempFile.delete()) {
                logger.log(Level.WARNING, "could not delete " + tempFile);
            }
        }
        tempFiles.clear();
        fontFiles.clear();
        fontMap.clear();
    }

    private static class FontEntry {
        String overrideName;
        File file;
    }

    private final List<FontEntry> fontFiles = new ArrayList<>();

    private final List<File> tempFiles = new ArrayList<>();

    private final Map<String, PDFont> fontMap = new HashMap<>();

    /**
     * Register a font. If possible, try to use a font file, i.e.
     * {@link #registerFont(String, File)}. This method will lead to the creation of
     * a temporary file which stores the font data.
     *
     * @param fontName   the name of the font to use. If null, the name is taken from the
     *                   font.
     * @param fontStream the input stream of the font. This file must be a ttf/otf file!
     *                   You have to close the stream outside, this method will not close
     *                   the stream.
     * @throws IOException when something goes wrong with reading the font or writing the
     *                     font to the content stream of the PDF:
     */
    public void registerFont(String fontName, InputStream fontStream) throws IOException {
        File fontFile = File.createTempFile("pdfboxgfx2dfont", ".ttf");
        try (FileOutputStream out = new FileOutputStream(fontFile)) {
            IOUtils.copy(fontStream, out);
        }
        fontFile.deleteOnExit();
        tempFiles.add(fontFile);
        registerFont(fontName, fontFile);
    }

    /**
     * Register a font.
     *
     * @param fontName the name of the font to use. If null, the name is taken from the
     *                 font.
     * @param fontFile the font file. This file must exist for the live time of this
     *                 object, as the font data will be read lazy on demand
     */
    @SuppressWarnings("WeakerAccess")
    public void registerFont(String fontName, File fontFile) {
        if (!fontFile.exists())
            throw new IllegalArgumentException("Font " + fontFile + " does not exist!");
        FontEntry entry = new FontEntry();
        entry.overrideName = fontName;
        entry.file = fontFile;
        fontFiles.add(entry);
    }

    /**
     * Override for registerFont(null,fontFile)
     *
     * @param fontFile the font file
     */
    @SuppressWarnings("WeakerAccess")
    public void registerFont(File fontFile) {
        registerFont(null, fontFile);
    }

    /**
     * Override for registerFont(null,fontStream)
     *
     * @param fontStream the font file
     * @throws IOException when something goes wrong with reading the font or writing the
     *                     font to the content stream of the PDF:
     */
    public void registerFont(InputStream fontStream) throws IOException {
        registerFont(null, fontStream);
    }

    /**
     * Register a font which is already associated with the PDDocument
     *
     * @param name the name of the font as returned by
     *             {@link Font#getFontName()}. This name is used for the
     *             mapping the java.awt.Font to this PDFont.
     * @param font the PDFont to use. This font must be loaded in the current
     *             document.
     */
    @SuppressWarnings("WeakerAccess")
    public void registerFont(String name, PDFont font) {
        fontMap.put(name, font);
    }

    /**
     * @return true if the font mapping is populated on demand. This is usually only
     * the case if this class has been derived. The default implementation
     * just checks for this.
     */
    protected boolean hasDynamicFontMapping() {
        return getClass() != DefaultFontDrawer.class;
    }

    @Override
    public boolean canDrawText(AttributedCharacterIterator iterator, FontDrawerEnvironment env)
            throws IOException, FontFormatException {
        if (fontMap.size() == 0 && fontFiles.size() == 0 && !hasDynamicFontMapping()) {
            return false;
        }
        boolean run = true;
        StringBuilder sb = new StringBuilder();
        while (run) {
            Font attributeFont = (Font) iterator.getAttribute(TextAttribute.FONT);
            if (attributeFont == null) {
                attributeFont = env.getFont();
            }
            if (mapFont(attributeFont, env) == null) {
                return false;
            }
            if (iterator.getAttribute(TextAttribute.BACKGROUND) != null) {
                return false;
            }
            boolean isStrikeThrough =
                    TextAttribute.STRIKETHROUGH_ON.equals(iterator.getAttribute(TextAttribute.STRIKETHROUGH));
            boolean isUnderline =
                    TextAttribute.UNDERLINE_ON.equals(iterator.getAttribute(TextAttribute.UNDERLINE));
            boolean isLigatures =
                    TextAttribute.LIGATURES_ON.equals(iterator.getAttribute(TextAttribute.LIGATURES));
            if (isStrikeThrough || isUnderline || isLigatures) {
                return false;
            }
            run = iterateRun(iterator, sb);
            String s = sb.toString();
            int l = s.length();
            for (int i = 0; i < l; ) {
                int codePoint = s.codePointAt(i);
                switch (Character.getDirectionality(codePoint)) {
                    case Character.DIRECTIONALITY_LEFT_TO_RIGHT:
                    case Character.DIRECTIONALITY_EUROPEAN_NUMBER:
                    case Character.DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR:
                    case Character.DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR:
                    case Character.DIRECTIONALITY_WHITESPACE:
                    case Character.DIRECTIONALITY_COMMON_NUMBER_SEPARATOR:
                    case Character.DIRECTIONALITY_NONSPACING_MARK:
                    case Character.DIRECTIONALITY_BOUNDARY_NEUTRAL:
                    case Character.DIRECTIONALITY_PARAGRAPH_SEPARATOR:
                    case Character.DIRECTIONALITY_SEGMENT_SEPARATOR:
                    case Character.DIRECTIONALITY_OTHER_NEUTRALS:
                    case Character.DIRECTIONALITY_ARABIC_NUMBER:
                        break;
                    case Character.DIRECTIONALITY_RIGHT_TO_LEFT:
                    case Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC:
                    case Character.DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING:
                    case Character.DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE:
                    case Character.DIRECTIONALITY_POP_DIRECTIONAL_FORMAT:
                        return false;
                    default:
                        return false;
                }
                if (!attributeFont.canDisplay(codePoint)) {
                    return false;
                }
                i += Character.charCount(codePoint);
            }
        }
        return true;
    }

    @Override
    public void drawText(AttributedCharacterIterator iterator, FontDrawerEnvironment env)
            throws IOException, FontFormatException {
        PDPageContentStream contentStream = env.getContentStream();

        contentStream.beginText();

        Matrix textMatrix = new Matrix();
        textMatrix.scale(1, -1);
        contentStream.setTextMatrix(textMatrix);

        StringBuilder sb = new StringBuilder();
        boolean run = true;
        while (run) {
            Font attributeFont = (Font) iterator.getAttribute(TextAttribute.FONT);
            if (attributeFont == null) {
                attributeFont = env.getFont();
            }
            Number fontSize = ((Number) iterator.getAttribute(TextAttribute.SIZE));
            if (fontSize != null) {
                attributeFont = attributeFont.deriveFont(fontSize.floatValue());
            }
            PDFont font = applyFont(attributeFont, env);
            Paint paint = (Paint) iterator.getAttribute(TextAttribute.FOREGROUND);
            if (paint == null) {
                paint = env.getPaint();
            }
            boolean isStrikeThrough =
                    TextAttribute.STRIKETHROUGH_ON.equals(iterator.getAttribute(TextAttribute.STRIKETHROUGH));
            boolean isUnderline =
                    TextAttribute.UNDERLINE_ON.equals(iterator.getAttribute(TextAttribute.UNDERLINE));
            boolean isLigatures =
                    TextAttribute.LIGATURES_ON.equals(iterator.getAttribute(TextAttribute.LIGATURES));
            run = iterateRun(iterator, sb);
            String text = sb.toString();
            env.applyPaint(paint, null);
            try {
                showTextOnStream(env, contentStream, attributeFont, font, isStrikeThrough,
                        isUnderline, isLigatures, text);
            } catch (IllegalArgumentException e) {
                if (font instanceof PDType1Font && !font.isEmbedded()) {
                    try {
                        if (fallbackFontUnknownEncodings == null) {
                            fallbackFontUnknownEncodings = findFallbackFont(env);
                        }
                        if (fallbackFontUnknownEncodings != null) {
                            env.getContentStream().setFont(fallbackFontUnknownEncodings,
                                    attributeFont.getSize2D());
                            showTextOnStream(env, contentStream, attributeFont,
                                    fallbackFontUnknownEncodings, isStrikeThrough, isUnderline,
                                    isLigatures, text);
                            e = null;
                        }
                    } catch (IllegalArgumentException e1) {
                        e = e1;
                    }
                }
                if (e != null) {
                    logger.log(Level.WARNING, "PDFBoxGraphics: Can not map text " + text + " with font "
                            + attributeFont.getFontName() + ": " + e.getMessage());
                }
            }
        }
        contentStream.endText();
    }

    @Override
    public FontMetrics getFontMetrics(Font f, FontDrawerEnvironment env)
            throws IOException, FontFormatException {
        final FontMetrics fontMetrics = env.getCalculationGraphics().getFontMetrics(f);
        final PDFont pdFont = mapFont(f, env);
        if (pdFont == null) {
            return fontMetrics;
        }
        return new DefaultFontMetrics(f, fontMetrics, pdFont);
    }

    private PDFont fallbackFontUnknownEncodings;

    private PDFont findFallbackFont(FontDrawerEnvironment env) {
        String javaHome = System.getProperty("java.home", ".");
        String javaFontDir = javaHome + "/lib/fonts";
        String windir = System.getenv("WINDIR");
        if (windir == null) {
            windir = javaFontDir;
        }
        File[] paths = new File[]{new File(new File(windir), "fonts"),
                new File(System.getProperty("user.dir", ".")),
                // Mac Fonts
                new File("/Library/Fonts"), new File("/System/Library/Fonts/Supplemental/"),
                // Unix Fonts
                new File("/usr/share/fonts/truetype"), new File("/usr/share/fonts/truetype/dejavu"),
                new File("/usr/share/fonts/truetype/liberation"),
                new File("/usr/share/fonts/truetype/noto"), new File(javaFontDir)};
        for (String fontFileName : new String[]{"LucidaSansRegular.ttf", "arial.ttf", "Arial.ttf",
                "DejaVuSans.ttf", "LiberationMono-Regular.ttf", "NotoSerif-Regular.ttf",
                "Arial Unicode.ttf", "Tahoma.ttf"}) {
            for (File path : paths) {
                File arialFile = new File(path, fontFileName);
                if (arialFile.exists()) {
                    // We try to use the first font we can find and use.
                    PDType0Font pdType0Font = tryToLoadFont(env, arialFile);
                    if (pdType0Font != null) {
                        return pdType0Font;
                    }
                }
            }
        }
        return null;
    }

    private PDType0Font tryToLoadFont(FontDrawerEnvironment env, File foundFontFile) {
        try {
            return PDType0Font.load(env.getDocument(), foundFontFile);
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage(), e);
            // The font may be have a embed restriction.
            return null;
        }
    }

    private void showTextOnStream(FontDrawerEnvironment env,
                                  PDPageContentStream contentStream,
                                  Font attributeFont,
                                  PDFont font,
                                  boolean isStrikeThrough,
                                  boolean isUnderline,
                                  boolean isLigatures,
                                  String text) throws IOException {
        contentStream.showText(text);
    }

    private PDFont applyFont(Font font, FontDrawerEnvironment env)
            throws IOException, FontFormatException {
        PDFont fontToUse = mapFont(font, env);
        if (fontToUse == null) {
            fontToUse = CoreFontDrawer.chooseMatchingHelvetica(font);
        }
        env.getContentStream().setFont(fontToUse, font.getSize2D());
        return fontToUse;
    }

    /**
     * Try to map the java.awt.Font to a PDFont.
     *
     * @param font the java.awt.Font for which a mapping should be found
     * @param env  environment of the font mapper
     * @return the PDFont or null if none can be found.
     * @throws IOException         when the font can not be loaded
     * @throws FontFormatException when the font file can not be loaded
     */
    protected PDFont mapFont(Font font, FontDrawerEnvironment env)
            throws IOException, FontFormatException {
        for (FontEntry fontEntry : fontFiles) {
            if (fontEntry.overrideName == null) {
                Font javaFont = Font.createFont(Font.TRUETYPE_FONT, fontEntry.file);
                fontEntry.overrideName = javaFont.getFontName();
            }
            if (fontEntry.file.getName().toLowerCase(Locale.US).endsWith(".ttc")) {
                TrueTypeCollection collection = new TrueTypeCollection(fontEntry.file);
                collection.processAllFonts(ttf -> {
                    PDFont pdFont = PDType0Font.load(env.getDocument(), ttf, true);
                    fontMap.put(fontEntry.overrideName, pdFont);
                    fontMap.put(pdFont.getName(), pdFont);
                });
            } else {
                PDFont pdFont = PDType0Font.load(env.getDocument(), fontEntry.file);
                fontMap.put(fontEntry.overrideName, pdFont);
            }
        }
        fontFiles.clear();
        return fontMap.get(font.getFontName());
    }

    private boolean iterateRun(AttributedCharacterIterator iterator, StringBuilder sb) {
        sb.setLength(0);
        int charCount = iterator.getRunLimit() - iterator.getRunStart();
        while (charCount-- >= 0) {
            char c = iterator.current();
            iterator.next();
            if (c == AttributedCharacterIterator.DONE) {
                return false;
            } else {
                sb.append(c);
            }
        }
        return true;
    }

    /**
     * Find a PDFont for the given font object.
     * @param font font for which to find a suitable core font
     * @return null if no core font is found or a core font which does not
     * need to be embedded.
     */
    protected static PDFont mapToCoreFonts(Font font) {
        if (fontNameEqualsAnyOf(font, Font.SANS_SERIF, Font.DIALOG, Font.DIALOG_INPUT, "Arial", "Helvetica")) {
            return chooseMatchingHelvetica(font);
        }
        if (fontNameEqualsAnyOf(font, Font.MONOSPACED, "courier", "courier new")) {
            return chooseMatchingCourier(font);
        }
        if (fontNameEqualsAnyOf(font, Font.SERIF, "Times", "Times New Roman", "Times Roman")) {
            return chooseMatchingTimes(font);
        }
        if (fontNameEqualsAnyOf(font, "Symbol")) {
            return PDType1Font.SYMBOL;
        }
        if (fontNameEqualsAnyOf(font, "ZapfDingbats", "Dingbats")) {
            return PDType1Font.ZAPF_DINGBATS;
        }
        return null;
    }

    /**
     * Get a PDType1Font.HELVETICA-variant, which matches the given font
     *
     * @param font Font to get the styles from
     * @return a PDFont Helvetica variant which matches the style in the given Font
     * object.
     */
    protected static PDFont chooseMatchingHelvetica(Font font) {
        if ((font.getStyle() & (Font.ITALIC | Font.BOLD)) == (Font.ITALIC | Font.BOLD)) {
            return PDType1Font.HELVETICA_BOLD_OBLIQUE;
        }
        if ((font.getStyle() & Font.ITALIC) == Font.ITALIC) {
            return PDType1Font.HELVETICA_OBLIQUE;
        }
        if ((font.getStyle() & Font.BOLD) == Font.BOLD) {
            return PDType1Font.HELVETICA_BOLD;
        }
        return PDType1Font.HELVETICA;
    }


    /**
     * Get a PDType1Font.COURIER-variant, which matches the given font
     *
     * @param font Font to get the styles from
     * @return a PDFont Courier variant which matches the style in the given Font
     * object.
     */
    protected static PDFont chooseMatchingCourier(Font font) {
        if ((font.getStyle() & (Font.ITALIC | Font.BOLD)) == (Font.ITALIC | Font.BOLD)) {
            return PDType1Font.COURIER_BOLD_OBLIQUE;
        }
        if ((font.getStyle() & Font.ITALIC) == Font.ITALIC) {
            return PDType1Font.COURIER_OBLIQUE;
        }
        if ((font.getStyle() & Font.BOLD) == Font.BOLD) {
            return PDType1Font.COURIER_BOLD;
        }
        return PDType1Font.COURIER;
    }

    /**
     * Get a PDType1Font.TIMES-variant, which matches the given font
     *
     * @param font Font to get the styles from
     * @return a PDFont Times variant which matches the style in the given Font
     * object.
     */
    protected static PDFont chooseMatchingTimes(Font font) {
        if ((font.getStyle() & (Font.ITALIC | Font.BOLD)) == (Font.ITALIC | Font.BOLD)) {
            return PDType1Font.TIMES_BOLD_ITALIC;
        }
        if ((font.getStyle() & Font.ITALIC) == Font.ITALIC) {
            return PDType1Font.TIMES_ITALIC;
        }
        if ((font.getStyle() & Font.BOLD) == Font.BOLD) {
            return PDType1Font.TIMES_BOLD;
        }
        return PDType1Font.TIMES_ROMAN;
    }

    private static boolean fontNameEqualsAnyOf(Font font, String... names) {
        String name = font.getName();
        for (String fontName : names) {
            if (fontName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
