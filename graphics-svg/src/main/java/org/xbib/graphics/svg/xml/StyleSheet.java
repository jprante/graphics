package org.xbib.graphics.svg.xml;

import java.util.HashMap;
import java.util.Map;

public class StyleSheet {

    Map<StyleSheetRule, String> ruleMap = new HashMap<>();

    public static StyleSheet parseSheet(String src) {
        return null;
    }

    public void addStyleRule(StyleSheetRule rule, String value) {
        ruleMap.put(rule, value);
    }

    public boolean getStyle(StyleAttribute attrib, String tagName, String cssClass) {
        StyleSheetRule rule = new StyleSheetRule(attrib.getName(), tagName, cssClass);
        String value = ruleMap.get(rule);
        if (value != null) {
            attrib.setStringValue(value);
            return true;
        }
        rule = new StyleSheetRule(attrib.getName(), null, cssClass);
        value = ruleMap.get(rule);
        if (value != null) {
            attrib.setStringValue(value);
            return true;
        }
        rule = new StyleSheetRule(attrib.getName(), tagName, null);
        value = ruleMap.get(rule);
        if (value != null) {
            attrib.setStringValue(value);
            return true;
        }
        return false;
    }
}
