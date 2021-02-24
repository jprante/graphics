package org.xbib.graphics.io.vector.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TestUtils {

    protected TestUtils() {
        throw new UnsupportedOperationException();
    }

    public static void assertTemplateEquals(Template expected, Template actual) {
        Iterator<Object> itExpected = expected.iterator();
        Iterator<Object> itActual = actual.iterator();
        while (itExpected.hasNext() && itActual.hasNext()) {
            Object lineExpected = itExpected.next();
            Object lineActual = itActual.next();
            if (lineExpected == null) {
                continue;
            }
            assertTrue(lineActual instanceof String, String.format("Line is of type %s, expected String.", lineActual.getClass()));
            if (lineExpected instanceof String) {
                assertEquals(lineExpected, lineActual, "not equal, actual is " + actual);
            } else if (lineExpected instanceof Pattern) {
                Pattern expectedPattern = (Pattern) lineExpected;
                Matcher matcher = expectedPattern.matcher((String) lineActual);
                assertTrue(matcher.matches(), String.format("Line didn't match pattern.\nExpected: \"%s\"\nActual: \"%s\"", matcher.pattern(), lineActual));
            }
        }
        assertEquals(expected.size(), actual.size(), "Wrong number of lines in template.");
    }

    @SuppressWarnings("serial")
    public static class Template extends LinkedList<Object> {
        public Template(Object[] lines) {
            Collections.addAll(this, lines);
        }

        public Template(Template[] templates) {
            for (Template template : templates) {
                addAll(template);
            }
        }
    }
}
