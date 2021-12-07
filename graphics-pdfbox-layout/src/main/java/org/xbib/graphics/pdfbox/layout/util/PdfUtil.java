package org.xbib.graphics.pdfbox.layout.util;

import org.xbib.graphics.pdfbox.layout.font.Font;
import org.xbib.graphics.pdfbox.layout.table.CouldNotDetermineStringWidthException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Provides some helping functions.
 */
public final class PdfUtil {

    public static final String NEW_LINE_REGEX = "\\r?\\n";

    /**
     * Computes the width of a String (in points).
     *
     * @param text     Text
     * @param font     Font of Text
     * @param fontSize FontSize of String
     * @return Width (in points)
     */
    public static float getStringWidth(String text, Font font, float fontSize) {
        return Arrays.stream(text.split(NEW_LINE_REGEX))
                .max(Comparator.comparing(String::length))
                .map(x -> {
                    try {
                        return getWidthOfStringWithoutNewlines(x, font, fontSize);
                    } catch (IOException exception) {
                        return 0f;
                    }
                })
                .orElseThrow(CouldNotDetermineStringWidthException::new);
    }

    private static float getWidthOfStringWithoutNewlines(String text, Font font, float fontSize) throws IOException {
        List<String> codePointsAsString = text.codePoints()
                .mapToObj(codePoint -> new String(new int[]{codePoint}, 0, 1))
                .collect(Collectors.toList());
        List<Float> widths = new ArrayList<>();
        for (String codepoint : codePointsAsString) {
            try {
                widths.add(font.getRegularFont().getStringWidth(codepoint) * fontSize / 1000F);
            } catch (final IllegalArgumentException | IOException e) {
                widths.add(font.getRegularFont().getStringWidth("–") * fontSize / 1000F);
            }
        }
        return widths.stream().reduce(0.0f, Float::sum);
    }

    /**
     * Computes the height of a font.
     *
     * @param font     Font
     * @param fontSize FontSize
     * @return Height of font
     */
    public static float getFontHeight(Font font, float fontSize) {
        return font.getRegularFont().getFontDescriptor().getCapHeight() * fontSize / 1000F;
    }

    /**
     * Split a text into multiple lines to prevent a text-overflow.
     *
     * @param text     Text
     * @param font     Used font
     * @param fontSize Used fontSize
     * @param maxWidth Maximal width of resulting text-lines
     * @return A list of lines, where all are smaller than maxWidth
     */
    public static List<String> getOptimalTextBreakLines(String text, Font font, float fontSize, float maxWidth) {
        List<String> result = new ArrayList<>();
        for (String line : text.split(NEW_LINE_REGEX)) {
            if (PdfUtil.doesTextLineFit(line, font, fontSize, maxWidth)) {
                result.add(line);
            } else {
                result.addAll(PdfUtil.wrapLine(line, font, fontSize, maxWidth));
            }
        }
        return result;
    }

    private static List<String> wrapLine(String line, Font font, float fontSize, float maxWidth) {
        if (doesTextLineFit(line, font, fontSize, maxWidth)) {
            return Collections.singletonList(line);
        }
        List<String> goodLines = new ArrayList<>();
        Stack<String> allWords = new Stack<>();
        Arrays.asList(line.split("(?<=[\\\\. ,-])")).forEach(allWords::push);
        Collections.reverse(allWords);
        while (!allWords.empty()) {
            goodLines.add(buildALine(allWords, font, fontSize, maxWidth));
        }
        return goodLines;
    }

    private static List<String> splitBySize(String line, Font font, float fontSize, float maxWidth) {
        List<String> returnList = new ArrayList<>();
        for (int i = line.length() - 1; i > 0; i--) {
            String fittedNewLine = line.substring(0, i) + "-";
            String remains = line.substring(i);
            if (PdfUtil.doesTextLineFit(fittedNewLine, font, fontSize, maxWidth)) {
                returnList.add(fittedNewLine);
                returnList.addAll(PdfUtil.wrapLine(remains, font, fontSize, maxWidth));
                break;
            }
        }
        return returnList;
    }

    private static String buildALine(Stack<String> words, Font font, float fontSize, float maxWidth) {
        StringBuilder line = new StringBuilder();
        float width = 0;
        while (!words.empty()) {
            float nextWordWidth = getStringWidth(words.peek(), font, fontSize);
            if (line.length() == 0 && words.peek().length() == 1 && nextWordWidth > maxWidth) {
                return words.pop();
            }
            if (doesTextLineFit(width + nextWordWidth, maxWidth)) {
                line.append(words.pop());
                width += nextWordWidth;
            } else {
                break;
            }
        }
        if (width == 0 && !words.empty()) {
            List<String> cutBySize = splitBySize(words.pop(), font, fontSize, maxWidth);
            Collections.reverse(cutBySize);
            cutBySize.forEach(words::push);
            return buildALine(words, font, fontSize, maxWidth);
        }
        return line.toString().trim();
    }

    private static boolean doesTextLineFit(String textLine, Font font, float fontSize, float maxWidth) {
        return doesTextLineFit(PdfUtil.getStringWidth(textLine, font, fontSize), maxWidth);
    }

    private static boolean doesTextLineFit(float stringWidth, float maxWidth) {
        if (isEqualInEpsilon(stringWidth, maxWidth)) {
            return true;
        }
        return maxWidth > stringWidth;
    }

    private static boolean isEqualInEpsilon(float x, float y) {
        return Math.abs(y - x) < 0.0001;
    }
}
