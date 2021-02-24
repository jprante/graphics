package org.xbib.graphics.chart.io;

import org.xbib.graphics.chart.xy.XYSeries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

/**
 * This class is used to export Chart data to a path.
 */
public class CSVExporter {

    private final static String LF = System.getProperty("line.separator");

    public static void writeCSVRows(XYSeries series, Path path) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path),
                StandardCharsets.UTF_8))) {
            String csv = join(series.getXData()) + LF;
            bufferedWriter.write(csv);
            csv = join(series.getYData()) + LF;
            bufferedWriter.write(csv);
            if (series.getExtraValues() != null) {
                csv = join(series.getExtraValues()) + LF;
                bufferedWriter.write(csv);
            }
        }
    }

    public static void writeCSVColumns(XYSeries series, Path path) throws IOException {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(path),
                StandardCharsets.UTF_8))) {
            Collection<?> xData = series.getXData();
            Collection<? extends Number> yData = series.getYData();
            Collection<? extends Number> errorBarData = series.getExtraValues();
            Iterator<?> itrx = xData.iterator();
            Iterator<? extends Number> itry = yData.iterator();
            Iterator<? extends Number> itrErrorBar = null;
            if (errorBarData != null) {
                itrErrorBar = errorBarData.iterator();
            }
            while (itrx.hasNext()) {
                Number xDataPoint = (Number) itrx.next();
                Number yDataPoint = itry.next();
                Number errorBarValue = null;
                if (itrErrorBar != null) {
                    errorBarValue = itrErrorBar.next();
                }
                StringBuilder sb = new StringBuilder();
                sb.append(xDataPoint).append(",");
                sb.append(yDataPoint).append(",");
                if (errorBarValue != null) {
                    sb.append(errorBarValue).append(",");
                }
                sb.append(LF);
                bufferedWriter.write(sb.toString());
            }
        }
    }

    private static String join(Collection<?> collection) {
        if (collection == null) {
            return null;
        }
        Iterator<?> iterator = collection.iterator();
        if (!iterator.hasNext()) {
            return "";
        }
        Object first = iterator.next();
        if (!iterator.hasNext()) {
            return first == null ? "" : first.toString();
        }
        StringBuilder sb = new StringBuilder();
        if (first != null) {
            sb.append(first);
        }
        while (iterator.hasNext()) {
            sb.append(",");
            Object obj = iterator.next();
            if (obj != null) {
                sb.append(obj);
            }
        }
        return sb.toString();
    }
}
