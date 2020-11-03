package org.xbib.graphics.chart.io.vector.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Abstract class that contains utility functions for working with data
 * collections like maps or lists.
 */
public abstract class DataUtils {
    /**
     * Default constructor that prevents creation of class.
     */
    protected DataUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a mapping from two arrays, one with keys, one with values.
     *
     * @param <K>    Data type of the keys.
     * @param <V>    Data type of the values.
     * @param keys   Array containing the keys.
     * @param values Array containing the values.
     * @return Map with keys and values from the specified arrays.
     */
    public static <K, V> Map<K, V> map(K[] keys, V[] values) {
        // Check for valid parameters
        if (keys.length != values.length) {
            throw new IllegalArgumentException(
                    "Cannot create a Map: " +
                            "The number of keys and values differs.");
        }
        // Fill map with keys and values
        Map<K, V> map = new LinkedHashMap<K, V>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            K key = keys[i];
            V value = values[i];
            map.put(key, value);
        }
        return map;
    }

    /**
     * Returns a string containing all elements concatenated by a specified
     * separator.
     *
     * @param separator Separator string.
     * @param elements  List of elements that should be concatenated.
     * @return a concatenated string.
     */
    public static String join(String separator, List<?> elements) {
        if (elements == null || elements.size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(elements.size() * 3);
        int i = 0;
        for (Object elem : elements) {
            if (separator.length() > 0 && i++ > 0) {
                sb.append(separator);
            }
            sb.append(format(elem));
        }
        return sb.toString();
    }

    /**
     * Returns a string containing all elements concatenated by a specified
     * separator.
     *
     * @param separator Separator string.
     * @param elements  Array of elements that should be concatenated.
     * @return a concatenated string.
     */
    public static String join(String separator, Object[] elements) {
        if (elements == null || elements.length == 0) {
            return "";
        }
        return join(separator, Arrays.asList(elements));
    }

    /**
     * Returns a string containing all double numbers concatenated by a
     * specified separator.
     *
     * @param separator Separator string.
     * @param elements  Array of double numbers that should be concatenated.
     * @return a concatenated string.
     */
    public static String join(String separator, double[] elements) {
        if (elements == null || elements.length == 0) {
            return "";
        }
        List<Double> list = new ArrayList<Double>(elements.length);
        for (Double element : elements) {
            list.add(element);
        }
        return join(separator, list);
    }

    /**
     * Returns a string containing all float numbers concatenated by a
     * specified separator.
     *
     * @param separator Separator string.
     * @param elements  Array of float numbers that should be concatenated.
     * @return a concatenated string.
     */
    public static String join(String separator, float[] elements) {
        if (elements == null || elements.length == 0) {
            return "";
        }
        List<Float> list = new ArrayList<Float>(elements.length);
        for (Float element : elements) {
            list.add(element);
        }
        return join(separator, list);
    }

    /**
     * Returns the largest of all specified values.
     *
     * @param values Several integer values.
     * @return largest value.
     */
    public static int max(int... values) {
        int max = values[0];
        for (int i = 1; i < values.length; i++) {
            if (values[i] > max) {
                max = values[i];
            }
        }
        return max;
    }

    /**
     * Copies data from an input stream to an output stream using a buffer of
     * specified size.
     *
     * @param in         Input stream.
     * @param out        Output stream.
     * @param bufferSize Size of the copy buffer.
     * @throws IOException when an error occurs while copying.
     */
    public static void transfer(InputStream in, OutputStream out, int bufferSize)
            throws IOException {
        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
    }

    /**
     * Returns a formatted string of the specified number. All trailing zeroes
     * or decimal points will be stripped.
     *
     * @param number Number to convert to a string.
     * @return A formatted string.
     */
    public static String format(Number number) {
        String formatted;
        if (number instanceof Double || number instanceof Float) {
            formatted = Double.toString(number.doubleValue())
                    .replaceAll("\\.0+$", "")
                    .replaceAll("(\\.[0-9]*[1-9])0+$", "$1");
        } else {
            formatted = number.toString();
        }
        return formatted;
    }

    /**
     * Returns a formatted string of the specified object.
     *
     * @param obj Object to convert to a string.
     * @return A formatted string.
     */
    public static String format(Object obj) {
        if (obj instanceof Number) {
            return format((Number) obj);
        } else {
            return obj.toString();
        }
    }

    /**
     * Converts an array of {@code float} numbers to a list of {@code Float}s.
     * The list will be empty if the array is empty or {@code null}.
     *
     * @param elements Array of float numbers.
     * @return A list with all numbers as {@code Float}.
     */
    public static List<Float> asList(float[] elements) {
        int size = (elements != null) ? elements.length : 0;
        List<Float> list = new ArrayList<Float>(size);
        if (elements != null) {
            for (Float elem : elements) {
                list.add(elem);
            }
        }
        return list;
    }

    /**
     * Converts an array of {@code double} numbers to a list of {@code Double}s.
     * The list will be empty if the array is empty or {@code null}.
     *
     * @param elements Array of double numbers.
     * @return A list with all numbers as {@code Double}.
     */
    public static List<Double> asList(double[] elements) {
        int size = (elements != null) ? elements.length : 0;
        List<Double> list = new ArrayList<Double>(size);
        if (elements != null) {
            for (Double elem : elements) {
                list.add(elem);
            }
        }
        return list;
    }

    /**
     * Removes the specified trailing pattern from a string.
     *
     * @param s      string.
     * @param substr trailing pattern.
     * @return A string without the trailing pattern.
     */
    public static String stripTrailing(String s, String substr) {
        return s.replaceAll("(" + Pattern.quote(substr) + ")+$", "");
    }
}
