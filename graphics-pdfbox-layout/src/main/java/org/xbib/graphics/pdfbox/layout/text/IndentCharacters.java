package org.xbib.graphics.pdfbox.layout.text;

import org.xbib.graphics.pdfbox.layout.font.FontDescriptor;
import org.xbib.graphics.pdfbox.layout.util.Enumerator;
import org.xbib.graphics.pdfbox.layout.util.EnumeratorFactory;
import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container class for current supported indentation control characters.
 */
public class IndentCharacters {

    /**
     * The factory for indent control characters.
     */
    public static ControlCharacters.ControlCharacterFactory INDENT_FACTORY = new IndentCharacterFactory();

    /**
     * Represent un-indentation, means effectively indent of 0.
     */
    public static IndentCharacter UNINDENT_CHARACTER = new IndentCharacter("0", "0", "pt");

    /**
     * An <code>--{7em}</code> indicates an indentation of 7 characters in markup,
     * where the number, the unit, and the brackets are optional. Default
     * indentation is 4 characters, default unit is <code>7em</code> It can be
     * escaped with a backslash ('\').
     */
    public static class IndentCharacter extends ControlCharacter {

        protected int level = 1;
        protected float indentWidth = 4;
        protected SpaceUnit indentUnit = SpaceUnit.em;

        public IndentCharacter(String level,
                               String indentWidth,
                               String indentUnit) {
            super("INDENT", IndentCharacterFactory.TO_ESCAPE);
            try {
                this.level = level == null ? 0 : level.length() + 1;
            } catch (NumberFormatException e) {
            }
            try {
                this.indentUnit = indentUnit == null ? SpaceUnit.em : SpaceUnit
                        .valueOf(indentUnit);
            } catch (NumberFormatException e) {
            }
            float defaultIndent = this.indentUnit == SpaceUnit.em ? 4 : 10;
            try {
                this.indentWidth = indentWidth == null ? defaultIndent
                        : Integer.parseInt(indentWidth);
            } catch (NumberFormatException e) {
            }

        }

        /**
         * @return the level of indentation, where 0 means no indent.
         */
        public int getLevel() {
            return level;
        }

        /**
         * @return the next label to use on a subsequent indent. Makes only
         * sense for enumerating indents.
         */
        protected String nextLabel() {
            return "";
        }

        /**
         * Creates the actual {@link Indent} fragment from this control
         * character.
         *
         * @param descriptor the current font size, the current font.
         * @param color    the color to use.
         * @return the new Indent.
         */
        public Indent createNewIndent(FontDescriptor descriptor, Color color) {
            return new Indent(nextLabel(), level * indentWidth, indentUnit, descriptor, Alignment.RIGHT, color);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((indentUnit == null) ? 0 : indentUnit.hashCode());
            result = prime * result + Float.floatToIntBits(indentWidth);
            result = prime * result + level;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            IndentCharacter other = (IndentCharacter) obj;
            if (indentUnit != other.indentUnit) {
                return false;
            }
            if (Float.floatToIntBits(indentWidth) != Float
                    .floatToIntBits(other.indentWidth)) {
                return false;
            }
            return level == other.level;
        }

    }

    /**
     * An <code>-+{--:7em}</code> indicates a list indentation of 7 characters in
     * markup, using <code>--</code> as the bullet. The number, the unit, bullet
     * character and the brackets are optional. Default indentation is 4
     * characters, default unit is <code>em</code> and the default bullet
     * depends on {@link #getBulletCharacter(int)}. It can be
     * escaped with a backslash ('\').
     */
    public static class ListCharacter extends IndentCharacter {

        protected String label;

        protected ListCharacter(String level, String indentWidth,
                                String indentUnit, String bulletCharacter) {
            super(level, indentWidth, indentUnit);
            if (bulletCharacter != null) {
                label = bulletCharacter;
                if (!label.endsWith(" ")) {
                    label += " ";
                }
            } else {
                label = getBulletCharacter(getLevel()) + " ";
            }
        }

        @Override
        protected String nextLabel() {
            return label;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + ((label == null) ? 0 : label.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ListCharacter other = (ListCharacter) obj;
            if (label == null) {
                return other.label == null;
            } else return label.equals(other.label);
        }

    }

    /**
     * An <code>-#{a):7em}</code> indicates an enumeration indentation of 7
     * characters in markup, using <code>a)...b)...etc</code> as the
     * enumeration. The number, the unit, enumeration type/separator, and the
     * brackets are optional. Default indentation is 4 characters, default unit is
     * <code>em</code>. Default enumeration are arabic numbers, the separator
     * depends on the enumerator by default ('.' for arabic). For available
     * enumerators see {@link EnumeratorFactory}.It can be escaped with a
     * backslash ('\').
     */
    public static class EnumerationCharacter extends IndentCharacter {

        protected Enumerator enumerator;
        protected String separator;

        protected EnumerationCharacter(String level, String indentWidth,
                                       String indentUnit, String enumerationType, String separator) {
            super(level, indentWidth, indentUnit);

            if (enumerationType == null) {
                enumerationType = "1";
            }
            enumerator = EnumeratorFactory.createEnumerator(enumerationType);
            this.separator = separator != null ? separator : enumerator
                    .getDefaultSeperator();
        }

        @Override
        protected String nextLabel() {
            String next = enumerator.next();
            StringBuilder bob = new StringBuilder(next.length()
                    + separator.length() + 1);
            bob.append(next);
            bob.append(separator);
            if (!separator.endsWith(" ")) {
                bob.append(" ");
            }
            return bob.toString();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + ((enumerator == null) ? 0 : enumerator.hashCode());
            result = prime * result
                    + ((separator == null) ? 0 : separator.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            EnumerationCharacter other = (EnumerationCharacter) obj;
            if (enumerator == null) {
                if (other.enumerator != null) {
                    return false;
                }
            } else if (other.enumerator == null) {
                return false;
            } else if (!enumerator.getClass().equals(
                    other.enumerator.getClass())) {
                return false;
            }
            if (separator == null) {
                return other.separator == null;
            } else return separator.equals(other.separator);
        }

    }

    private static class IndentCharacterFactory implements
            ControlCharacters.ControlCharacterFactory {

        private final static Pattern PATTERN = Pattern
                .compile("^-(!)|^([ ]*)-(-)(\\{(\\d*)(em|pt)?\\})?|^([ ]*)-(\\+)(\\{(.+)?:(\\d*)(em|pt)?\\})?|^([ ]*)-(#)(\\{((?!:).)?(.+)?:((\\d*))((em|pt))?\\})?");
        private final static Pattern UNESCAPE_PATTERN = Pattern
                .compile("^\\\\([ ]*-[-|+|#])");

        private final static String TO_ESCAPE = "--";

        @Override
        public ControlCharacter createControlCharacter(String text,
                                                       Matcher matcher, final List<CharSequence> charactersSoFar) {
            if ("!".equals(matcher.group(1))) {
                return UNINDENT_CHARACTER;
            }

            if ("-".equals(matcher.group(3))) {
                return new IndentCharacter(matcher.group(2), matcher.group(5),
                        matcher.group(6));
            }

            if ("+".equals(matcher.group(8))) {
                return new ListCharacter(matcher.group(7), matcher.group(11),
                        matcher.group(12), matcher.group(10));
            }

            if ("#".equals(matcher.group(14))) {
                return new EnumerationCharacter(matcher.group(13),
                        matcher.group(18), matcher.group(20),
                        matcher.group(16), matcher.group(17));
            }

            throw new IllegalArgumentException("unkown indentation " + text);
        }

        @Override
        public Pattern getPattern() {
            return PATTERN;
        }

        @Override
        public String unescape(String text) {
            Matcher matcher = UNESCAPE_PATTERN.matcher(text);
            if (!matcher.find()) {
                return text;
            }
            return matcher.group(1) + text.substring(matcher.end());
        }

        @Override
        public boolean patternMatchesBeginOfLine() {
            return true;
        }
    }

    private static String getBulletCharacter(final int level) {
        if (level % 2 == 1) {
            return System.getProperty("pdfbox.layout.bullet.odd", BULLET);
        }
        return System.getProperty("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
    }

    private static final String BULLET = "\u2022";

    private static final String DOUBLE_ANGLE = "\u00bb";

}
