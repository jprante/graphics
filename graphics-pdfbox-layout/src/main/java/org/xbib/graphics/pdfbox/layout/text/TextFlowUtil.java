package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotatedStyledText;
import org.xbib.graphics.pdfbox.layout.text.annotations.Annotation;
import org.xbib.graphics.pdfbox.layout.text.annotations.AnnotationCharacters;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

public class TextFlowUtil {

    public static TextFlow createTextFlow(String text, FontDescriptor descriptor) {
        final Iterable<CharSequence> parts = fromPlainText(text);
        return createTextFlow(parts, descriptor);
    }

    /**
     * Creates a text flow from the given text. The text may contain line
     * breaks, and also supports some markup for creating bold and italic fonts.
     * The following raw text
     *
     * <pre>
     * Markup supports *bold*, _italic_, and *even _mixed* markup_.
     * </pre>
     * <p>
     * is rendered like this:
     *
     * <pre>
     * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</b> markup</em>.
     * </pre>
     * <p>
     * Use backslash to escape special characters '*', '_' and '\' itself:
     *
     * <pre>
     * Escape \* with \\\* and \_ with \\\_ in markup.
     * </pre>
     * <p>
     * is rendered like this:
     *
     * <pre>
     * Escape * with \* and _ with \_ in markup.
     * </pre>
     *
     * @param markup         the markup text.
     * @param descriptor       the font size to use, and the font.
     * @return the created text flow.
     */
    public static TextFlow createTextFlowFromMarkup(String markup, FontDescriptor descriptor) {
        final Iterable<CharSequence> parts = fromMarkup(markup);
        return createTextFlow(parts, descriptor);
    }

    /**
     * Actually creates the text flow from the given (markup) text.
     *
     * @param parts          the parts to create the text flow from.
     * @param descriptor       the font size to use.
     * @return the created text flow.
     */
    protected static TextFlow createTextFlow(Iterable<CharSequence> parts, FontDescriptor descriptor) {
        final TextFlow result = new TextFlow();
        boolean bold = false;
        boolean italic = false;
        Color color = Color.black;
        ControlCharacters.MetricsControlCharacter metricsControl = null;
        Map<Class<? extends Annotation>, Annotation> annotationMap = new HashMap<>();
        Stack<IndentCharacters.IndentCharacter> indentStack = new Stack<>();
        for (final CharSequence fragment : parts) {
            if (fragment instanceof ControlCharacter) {
                if (fragment instanceof ControlCharacters.NewLineControlCharacter) {
                    result.add(new NewLine(descriptor));
                }
                if (fragment instanceof ControlCharacters.BoldControlCharacter) {
                    bold = !bold;
                }
                if (fragment instanceof ControlCharacters.ItalicControlCharacter) {
                    italic = !italic;
                }
                if (fragment instanceof ControlCharacters.ColorControlCharacter) {
                    color = ((ControlCharacters.ColorControlCharacter) fragment).getColor();
                }
                if (fragment instanceof AnnotationCharacters.AnnotationControlCharacter) {
                    AnnotationCharacters.AnnotationControlCharacter<?> annotationControlCharacter = (AnnotationCharacters.AnnotationControlCharacter<?>) fragment;
                    if (annotationMap.containsKey(annotationControlCharacter.getAnnotationType())) {
                        annotationMap.remove(annotationControlCharacter.getAnnotationType());
                    } else {
                        annotationMap.put(annotationControlCharacter.getAnnotationType(),
                                annotationControlCharacter.getAnnotation());
                    }
                }
                if (fragment instanceof ControlCharacters.MetricsControlCharacter) {
                    if (metricsControl != null && metricsControl.toString().equals(fragment.toString())) {
                        metricsControl = null;
                    } else {
                        metricsControl = (ControlCharacters.MetricsControlCharacter) fragment;
                    }
                }
                if (fragment instanceof IndentCharacters.IndentCharacter) {
                    IndentCharacters.IndentCharacter currentIndent = (IndentCharacters.IndentCharacter) fragment;
                    if (currentIndent.getLevel() == 0) {
                        indentStack.clear();
                        result.add(Indent.UNINDENT);
                    } else {
                        IndentCharacters.IndentCharacter last = null;
                        while (!indentStack.isEmpty()
                                && indentStack.peek() != null
                                && currentIndent.getLevel() <= indentStack
                                .peek().getLevel()) {
                            last = indentStack.pop();
                        }
                        if (last != null && last.equals(currentIndent)) {
                            currentIndent = last;
                        }
                        indentStack.push(currentIndent);
                        FontDescriptor fontDescriptor =
                                new FontDescriptor(descriptor.getFont(), descriptor.getSize(), bold, italic);
                        result.add(currentIndent.createNewIndent(fontDescriptor, color));
                    }
                }
            } else {
                float baselineOffset = 0;
                float currentFontSize = descriptor.getSize();
                if (metricsControl != null) {
                    baselineOffset = metricsControl.getBaselineOffsetScale() * descriptor.getSize();
                    currentFontSize *= metricsControl.getFontScale();
                }
                FontDescriptor fontDescriptor = new FontDescriptor(descriptor.getFont(), currentFontSize, bold, italic);
                if (annotationMap.isEmpty()) {
                    StyledText styledText = new StyledText(fragment.toString(), fontDescriptor,
                            color, baselineOffset, 0, 0);
                    result.add(styledText);
                } else {
                    AnnotatedStyledText styledText =
                            new AnnotatedStyledText(fragment.toString(), fontDescriptor,
                                    color, baselineOffset, 0, 0, annotationMap.values());
                    result.add(styledText);
                }
            }
        }
        return result;
    }

    /**
     * Creates a char sequence where new-line is replaced by the corresponding
     * {@link ControlCharacter}.
     *
     * @param text the original text.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromPlainText(final CharSequence text) {
        return fromPlainText(Collections.singleton(text));
    }

    /**
     * Creates a char sequence where new-line is replaced by the corresponding
     * {@link ControlCharacter}.
     *
     * @param text the original text.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromPlainText(
            final Iterable<CharSequence> text) {
        Iterable<CharSequence> result = splitByControlCharacter(
                ControlCharacters.NEWLINE_FACTORY, text);
        result = unescapeBackslash(result);
        return result;
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     *
     * @param markup the markup.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(final CharSequence markup) {
        return fromMarkup(Collections.singleton(markup));
    }

    /**
     * Creates a char sequence where new-line, asterisk and underscore are
     * replaced by their corresponding {@link ControlCharacter}.
     *
     * @param markup the markup.
     * @return the create char sequence.
     */
    public static Iterable<CharSequence> fromMarkup(Iterable<CharSequence> markup) {
        Iterable<CharSequence> text = markup;
        text = splitByControlCharacter(ControlCharacters.NEWLINE_FACTORY, text);
        text = splitByControlCharacter(ControlCharacters.METRICS_FACTORY, text);
        text = splitByControlCharacter(ControlCharacters.BOLD_FACTORY, text);
        text = splitByControlCharacter(ControlCharacters.ITALIC_FACTORY, text);
        text = splitByControlCharacter(ControlCharacters.COLOR_FACTORY, text);
        for (AnnotationCharacters.AnnotationControlCharacterFactory<?> annotationControlCharacterFactory : AnnotationCharacters
                .getFactories()) {
            text = splitByControlCharacter(annotationControlCharacterFactory, text);
        }
        text = splitByControlCharacter(IndentCharacters.INDENT_FACTORY, text);
        text = unescapeBackslash(text);
        return text;
    }

    /**
     * Splits the sequence by the given control character and replaces its
     * markup representation by the {@link ControlCharacter}.
     *
     * @param controlCharacterFactory the control character to split by.
     * @param markup                  the markup to split.
     * @return the splitted and replaced sequence.
     */
    protected static Iterable<CharSequence> splitByControlCharacter(
            ControlCharacters.ControlCharacterFactory controlCharacterFactory,
            final Iterable<CharSequence> markup) {
        List<CharSequence> result = new ArrayList<>();
        boolean beginOfLine = true;
        for (CharSequence current : markup) {
            if (current instanceof String) {
                String string = (String) current;
                int begin = 0;
                if (!controlCharacterFactory.patternMatchesBeginOfLine()
                        || beginOfLine) {
                    Matcher matcher = controlCharacterFactory.getPattern()
                            .matcher(string);
                    while (matcher.find()) {
                        String part = string.substring(begin, matcher.start());
                        begin = matcher.end();
                        if (!part.isEmpty()) {
                            String unescaped = controlCharacterFactory.unescape(part);
                            result.add(unescaped);
                        }
                        result.add(controlCharacterFactory.createControlCharacter(string, matcher, result));
                    }
                }
                if (begin < string.length()) {
                    String part = string.substring(begin);
                    String unescaped = controlCharacterFactory.unescape(part);
                    result.add(unescaped);
                }
                beginOfLine = false;
            } else {
                if (current instanceof ControlCharacters.NewLineControlCharacter) {
                    beginOfLine = true;
                }
                result.add(current);
            }
        }
        return result;
    }

    private static Iterable<CharSequence> unescapeBackslash(Iterable<CharSequence> chars) {
        List<CharSequence> result = new ArrayList<>();
        for (CharSequence current : chars) {
            if (current instanceof String) {
                result.add(ControlCharacters.unescapeBackslash((String) current));
            } else {
                result.add(current);
            }
        }
        return result;
    }
}
