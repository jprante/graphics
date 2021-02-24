package org.xbib.graphics.chart.formatter;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@SuppressWarnings("serial")
public class DateFormatter extends Format {

    private final String pattern;

    private final Locale locale;

    public DateFormatter(String pattern, Locale locale) {
        this.pattern = pattern;
        this.locale = locale;
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        if (obj instanceof ZonedDateTime) {
            ZonedDateTime zdt = (ZonedDateTime) obj;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern)
                    .withLocale(locale);
            toAppendTo.append(zdt.format(dtf));
        }
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}
