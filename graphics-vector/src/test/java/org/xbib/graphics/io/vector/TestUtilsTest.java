package org.xbib.graphics.io.vector;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestUtilsTest {

    @Test
    public void testParseXmlStartTag() throws Exception {
        String xmlTagName = "foo:bar.baz_tag";
        String xmlString;
        XMLFragment frag;
        xmlString = "<" + xmlTagName + ">";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.START_TAG, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
        xmlString = "< " + xmlTagName + "  >";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.START_TAG, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlEndTag() throws Exception {
        String xmlTagName = "foo:bar.baz_tag";
        String xmlString;
        XMLFragment frag;

        xmlString = "</" + xmlTagName + ">";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.END_TAG, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "</ " + xmlTagName + "  >";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.END_TAG, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlEmptyElement() throws Exception {
        String xmlTagName = "foo:bar.baz_tag";
        String xmlString;
        XMLFragment frag;

        xmlString = "<" + xmlTagName + "/>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.EMPTY_ELEMENT, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "< " + xmlTagName + "  />";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals(XMLFragment.FragmentType.EMPTY_ELEMENT, frag.type);
        assertTrue(frag.attributes.isEmpty());
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlCDATA() throws Exception {
        String xmlString;
        XMLFragment frag;

        xmlString = "<![CDATA[foo bar]]>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals("", frag.name);
        assertEquals(XMLFragment.FragmentType.CDATA, frag.type);
        assertEquals("foo bar", frag.attributes.get("value"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "<![CDATA[<nested>foo bar</nested>]]>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals("", frag.name);
        assertEquals(XMLFragment.FragmentType.CDATA, frag.type);
        assertEquals("<nested>foo bar</nested>", frag.attributes.get("value"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlDeclaration() throws Exception {
        String xmlString;
        XMLFragment frag;

        xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals("xml", frag.name);
        assertEquals(XMLFragment.FragmentType.DECLARATION, frag.type);
        assertEquals("1.0", frag.attributes.get("version"));
        assertEquals("UTF-8", frag.attributes.get("encoding"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlDoctype() throws Exception {
        String xmlString;
        XMLFragment frag;

        xmlString = "<!DOCTYPE html>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(XMLFragment.FragmentType.DOCTYPE, frag.type);
        assertEquals("html", frag.attributes.get("doctype 00"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1 Tiny//EN\"\n" +
                "\t\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny.dtd\">";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(XMLFragment.FragmentType.DOCTYPE, frag.type);
        assertEquals("svg", frag.attributes.get("doctype 00"));
        assertEquals("PUBLIC", frag.attributes.get("doctype 01"));
        assertEquals("-//W3C//DTD SVG 1.1 Tiny//EN", frag.attributes.get("doctype 02"));
        assertEquals("http://www.w3.org/Graphics/SVG/1.1/DTD/svg11-tiny.dtd", frag.attributes.get("doctype 03"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXmlComment() throws Exception {
        String xmlString;
        XMLFragment frag;

        xmlString = "<!-- foo bar -->";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals("", frag.name);
        assertEquals(XMLFragment.FragmentType.COMMENT, frag.type);
        assertEquals("foo bar", frag.attributes.get("value"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "<!-- <nested>foo bar</nested> -->";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals("", frag.name);
        assertEquals(XMLFragment.FragmentType.COMMENT, frag.type);
        assertEquals("<nested>foo bar</nested>", frag.attributes.get("value"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }

    @Test
    public void testParseXMLAttributesTag() throws Exception {
        String xmlTagName = "foo:bar.baz_tag";
        String xmlString;
        XMLFragment frag;

        xmlString = "<" + xmlTagName + " foo='bar'>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals("bar", frag.attributes.get("foo"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "<" + xmlTagName + " foo=\"bar\">";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals("bar", frag.attributes.get("foo"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);

        xmlString = "<" + xmlTagName + " foo=\"bar\" baz='qux'>";
        frag = XMLFragment.parse(xmlString, 0);
        assertEquals(xmlTagName, frag.name);
        assertEquals("bar", frag.attributes.get("foo"));
        assertEquals("qux", frag.attributes.get("baz"));
        assertEquals(0, frag.matchStart);
        assertEquals(xmlString.length(), frag.matchEnd);
    }


    public static class XMLFragment {

        private static final Pattern CDATA = Pattern.compile("\\s*<!\\[CDATA\\[(.*?)\\]\\]>");

        private static final Pattern COMMENT = Pattern.compile("\\s*<!--(.*?)-->");

        private static final Pattern TAG_BEGIN = Pattern.compile("\\s*<(/|\\?|!)?\\s*([^\\s>/\\?]+)");

        private static final Pattern TAG_END = Pattern.compile("\\s*(/|\\?)?>");

        private static final Pattern TAG_ATTRIBUTE = Pattern.compile("\\s*([^\\s>=]+)=(\"[^\"]*\"|'[^']*')");

        private static final Pattern DOCTYPE_PART = Pattern.compile("\\s*(\"[^\"]*\"|'[^']*'|[^\\s>]+)");

        public final String name;

        public final FragmentType type;

        public final Map<String, String> attributes;

        public final int matchStart;

        public final int matchEnd;

        public XMLFragment(String name, FragmentType type, Map<String, String> attributes,
                           int matchStart, int matchEnd) {
            this.name = name;
            this.type = type;
            this.attributes = Collections.unmodifiableMap(
                    new TreeMap<String, String>(attributes));
            this.matchStart = matchStart;
            this.matchEnd = matchEnd;
        }

        public static XMLFragment parse(String xmlString, int matchStart) {
            Map<String, String> attrs = new IdentityHashMap<String, String>();

            Matcher cdataMatch = CDATA.matcher(xmlString);
            cdataMatch.region(matchStart, xmlString.length());
            if (cdataMatch.lookingAt()) {
                attrs.put("value", cdataMatch.group(1));
                return new XMLFragment("", FragmentType.CDATA, attrs, matchStart, cdataMatch.end());
            }

            Matcher commentMatch = COMMENT.matcher(xmlString);
            commentMatch.region(matchStart, xmlString.length());
            if (commentMatch.lookingAt()) {
                attrs.put("value", commentMatch.group(1).trim());
                return new XMLFragment("", FragmentType.COMMENT, attrs, matchStart, commentMatch.end());
            }

            Matcher beginMatch = TAG_BEGIN.matcher(xmlString);
            beginMatch.region(matchStart, xmlString.length());
            if (!beginMatch.lookingAt()) {
                return null;
            }
            int matchEndPrev = beginMatch.end();

            String modifiers = beginMatch.group(1);
            String name = beginMatch.group(2);
            boolean endTag = "/".equals(modifiers);
            boolean declarationStart = "?".equals(modifiers);
            boolean doctype = "!".equals(modifiers) && "DOCTYPE".equals(name);

            if (doctype) {
                int partNo = 0;
                while (true) {
                    Matcher attrMatch = DOCTYPE_PART.matcher(xmlString);
                    attrMatch.region(matchEndPrev, xmlString.length());
                    if (!attrMatch.lookingAt()) {
                        break;
                    }
                    matchEndPrev = attrMatch.end();

                    String partValue = attrMatch.group(1);
                    if (partValue.startsWith("\"") || partValue.startsWith("'")) {
                        partValue = partValue.substring(1, partValue.length() - 1);
                    }

                    String partId = String.format("doctype %02d", partNo++);
                    attrs.put(partId, partValue);
                }
            } else {
                while (true) {
                    Matcher attrMatch = TAG_ATTRIBUTE.matcher(xmlString);
                    attrMatch.region(matchEndPrev, xmlString.length());
                    if (!attrMatch.lookingAt()) {
                        break;
                    }
                    matchEndPrev = attrMatch.end();

                    String attrName = attrMatch.group(1);
                    String attrValue = attrMatch.group(2);
                    attrValue = attrValue.substring(1, attrValue.length() - 1);
                    attrs.put(attrName, attrValue);
                }
            }

            Matcher endMatch = TAG_END.matcher(xmlString);
            endMatch.region(matchEndPrev, xmlString.length());
            if (!endMatch.lookingAt()) {
                throw new AssertionError(String.format("No tag end found: %s", xmlString.substring(0, matchEndPrev)));
            }
            matchEndPrev = endMatch.end();

            modifiers = endMatch.group(1);
            boolean emptyElement = "/".equals(modifiers);
            boolean declarationEnd = "?".equals(modifiers);

            FragmentType type = FragmentType.START_TAG;
            if (endTag) {
                type = FragmentType.END_TAG;
            } else if (emptyElement) {
                type = FragmentType.EMPTY_ELEMENT;
            } else if (declarationStart && declarationEnd) {
                type = FragmentType.DECLARATION;
            } else if (doctype) {
                type = FragmentType.DOCTYPE;
            }

            return new XMLFragment(name, type, attrs, matchStart, matchEndPrev);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof XMLFragment)) {
                return false;
            }
            XMLFragment frag = (XMLFragment) o;
            if (!type.equals(frag.type) || !name.equals(frag.name)) {
                return false;
            }
            Iterator<Map.Entry<String, String>> itThis = attributes.entrySet().iterator();
            Iterator<Map.Entry<String, String>> itFrag = frag.attributes.entrySet().iterator();
            while (itThis.hasNext() && itFrag.hasNext()) {
                Map.Entry<String, String> attrThis = itThis.next();
                Map.Entry<String, String> attrFrag = itFrag.next();
                if (!attrThis.getKey().equals(attrFrag.getKey()) ||
                        !attrThis.getValue().equals(attrFrag.getValue())) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            return type.hashCode() ^ attributes.hashCode();
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder("<");
            if (FragmentType.END_TAG.equals(type)) {
                s.append("/");
            } else if (FragmentType.DECLARATION.equals(type)) {
                s.append("?");
            }

            if (FragmentType.DOCTYPE.equals(type)) {
                s.append("!").append(name);
                for (String partValue : attributes.values()) {
                    s.append(" ").append(partValue);
                }
            } else {
                s.append(name);
                for (Map.Entry<String, String> attr : attributes.entrySet()) {
                    s.append(" ").append(attr.getKey()).append("=\"").append(attr.getValue()).append("\"");
                }
            }
            if (FragmentType.DECLARATION.equals(type)) {
                s.append("?");
            }
            s.append(">");
            return s.toString();
        }

        public enum FragmentType {
            START_TAG, END_TAG, EMPTY_ELEMENT, CDATA,
            DECLARATION, DOCTYPE, COMMENT
        }
    }
}
