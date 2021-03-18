package org.xbib.graphics.graph.jmathplot.io.files;

import org.xbib.graphics.graph.jmathplot.io.parser.ArrayString;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

/**
 * BSD License
 *
 * @author Yann RICHET
 */
public class ASCIIFile extends DataFile {

    public ASCIIFile(File f) {
        super(f);
    }

    public static String read(File f) {
        ASCIIFile af = new ASCIIFile(f);
        return af.read();
    }

    public static String[] readLines(File f) {
        ASCIIFile af = new ASCIIFile(f);
        return af.readLines();
    }

    public static String readLine(File f, int i) {
        ASCIIFile af = new ASCIIFile(f);
        return af.readLine(i);
    }

    public static double[] readDouble1DArray(File f) {
        return ArrayString.readString1DDouble(ASCIIFile.read(f));
    }

    public static double[][] readDoubleArray(File f) {
        return ArrayString.readStringDouble(ASCIIFile.read(f));
    }

    public static int[] readInt1DArray(File f) {
        return ArrayString.readString1DInt(ASCIIFile.read(f));
    }

    public static int[][] readIntArray(File f) {
        return ArrayString.readStringInt(ASCIIFile.read(f));
    }

    public static void write(File f, String t) {
        ASCIIFile af = new ASCIIFile(f);
        af.write(t, false);
    }

    public static void writeDoubleArray(File f, double[] array) {
        write(f, ArrayString.printDoubleArray(array));
    }

    public static void writeDoubleArray(File f, double[][] array) {
        write(f, ArrayString.printDoubleArray(array));
    }

    public static void writeIntArray(File f, int[] array) {
        write(f, ArrayString.printIntArray(array));
    }

    public static void writeIntArray(File f, int[][] array) {
        write(f, ArrayString.printIntArray(array));
    }

    public static void append(File f, String t) {
        ASCIIFile af = new ASCIIFile(f);
        af.write(t, true);
    }

    /**
     * Read an ASCII File
     *
     * @return String
     */
    public String read() {
        StringBuffer text = new StringBuffer((int) file.length());
        BufferedReader b = null;
        try {
            FileReader fr = new FileReader(file);
            b = new BufferedReader(fr);
            boolean eof = false;
            String line;
            String ret = "\n";
            while (!eof) {
                line = b.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    text.append(line);
                    text.append(ret);
                }
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("File " + file.getName()
                    + " is unreadable : " + e.toString());
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return text.toString();
    }

    /**
     * Read lines of an ASCII File
     *
     * @return an Array of String
     */
    public String[] readLines() {
        Vector<String> linesVector = new Vector<String>();
        BufferedReader b = null;
        try {
            FileReader fr = new FileReader(file);
            b = new BufferedReader(fr);
            boolean eof = false;
            while (!eof) {
                String line = b.readLine();
                if (line == null) {
                    eof = true;
                } else {
                    linesVector.add(line);
                }
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("File " + file.getName()
                    + " is unreadable : " + e.toString());
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }


        String[] lines = new String[linesVector.size()];
        for (int i = 0; i < lines.length; i++) {
            lines[i] = linesVector.get(i);
        }
        return lines;
    }

    /**
     * Read only one line in an ASCII File
     *
     * @param i line index
     * @return String
     */
    public String readLine(int i) {
        String line = "";
        BufferedReader b = null;
        try {
            FileReader fr = new FileReader(file);
            b = new BufferedReader(fr);
            boolean eof = false;
            for (int j = 0; j < i; j++) {
                if (eof) {
                    throw new IllegalArgumentException("Line " + i
                            + " is not found in the file " + file.getName()
                            + ".");
                }
                line = b.readLine();
                if (line == null) {
                    eof = true;
                }
            }
            line = b.readLine();

        } catch (IOException e) {
            throw new IllegalArgumentException("File " + file.getName()
                    + " is unreadable : " + e.toString());
        } finally {
            try {
                b.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return line;
    }

    /**
     * Write a text in an ASCII File
     *
     * @param text   String
     * @param append boolean
     */
    public void write(String text, boolean append) {
        if (file.exists() && !append) {
            System.out.println("Warning : the file " + file.getName()
                    + " already exists !");
        }
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(file, append);
            bw = new BufferedWriter(fw);
            bw.write(text);

        } catch (IOException e) {
            throw new IllegalArgumentException("File " + file.getName()
                    + " is unwritable : " + e.toString());
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void concatenate(File f1, File f2) {

        FileInputStream fis = null;
        BufferedInputStream bis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fis = new FileInputStream(f2);
            bis = new BufferedInputStream(fis);
            fos = new FileOutputStream(f1, true);
            bos = new BufferedOutputStream(fos);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
            }
        } catch (IOException e) {
            System.err.println("Concatenate: " + e);
        } finally {
            try {
                bis.close();
                fis.close();
                bos.close();
                fos.close();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        File f = new File("read.txt");
        System.out.println(ASCIIFile.read(f));

        String[] lines = ASCIIFile.readLines(f);
        for (int i = 0; i < lines.length; i++) {
            System.out.println("line " + i + " : " + lines[i]);
        }

        System.out.println(ASCIIFile.readLine(f, 0));
        System.out.println(ASCIIFile.readLine(f, lines.length - 1));

        ASCIIFile.append(new File("write.txt"), Calendar.getInstance().getTime().toString());
    }
}
