package org.xbib.graphics.imageio.plugins.png.pngj;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Some utility static methods for internal use.
 * Client code should not normally use this class
 */
public final class PngHelperInternal {

    /**
     * Default charset, used internally by PNG for several things
     */
    public static Charset charsetLatin1 = StandardCharsets.ISO_8859_1;
    /**
     * UTF-8 is only used for some chunks
     */
    public static Charset charsetUTF8 = StandardCharsets.UTF_8;

    /**
     * PNG magic bytes
     */
    public static byte[] getPngIdSignature() {
        return new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    }

    public static int doubleToInt100000(double d) {
        return (int) (d * 100000.0 + 0.5);
    }

    public static double intToDouble100000(int i) {
        return i / 100000.0;
    }

    public static int readByte(InputStream is) {
        try {
            return is.read();
        } catch (IOException e) {
            throw new PngjInputException("error reading byte", e);
        }
    }

    /**
     * -1 if eof
     * PNG uses "network byte order"
     */
    public static int readInt2(InputStream is) {
        try {
            int b1 = is.read();
            int b2 = is.read();
			if (b1 == -1 || b2 == -1) {
				return -1;
			}
            return (b1 << 8) | b2;
        } catch (IOException e) {
            throw new PngjInputException("error reading Int2", e);
        }
    }

    /**
     * -1 if eof
     * PNG uses "network byte order"
     */
    public static int readInt4(InputStream is) {
        try {
            int b1 = is.read();
            int b2 = is.read();
            int b3 = is.read();
            int b4 = is.read();
			if (b1 == -1 || b2 == -1 || b3 == -1 || b4 == -1) {
				return -1;
			}
            return (b1 << 24) | (b2 << 16) | (b3 << 8) + b4;
        } catch (IOException e) {
            throw new PngjInputException("error reading Int4", e);
        }
    }

    public static int readInt1fromByte(byte[] b, int offset) {
        return (b[offset] & 0xff);
    }

    public static int readInt2fromBytes(byte[] b, int offset) {
        return ((b[offset] & 0xff) << 8) | ((b[offset + 1] & 0xff));
    }

    public static int readInt4fromBytes(byte[] b, int offset) {
        return ((b[offset] & 0xff) << 24) | ((b[offset + 1] & 0xff) << 16) | ((b[offset + 2] & 0xff) << 8)
                | (b[offset + 3] & 0xff);
    }

    public static void writeByte(OutputStream os, byte b) {
        try {
            os.write(b);
        } catch (IOException e) {
            throw new PngjOutputException(e);
        }
    }

    public static void writeByte(OutputStream os, byte[] bs) {
        try {
            os.write(bs);
        } catch (IOException e) {
            throw new PngjOutputException(e);
        }
    }

    public static void writeInt2(OutputStream os, int n) {
        byte[] temp = {(byte) ((n >> 8) & 0xff), (byte) (n & 0xff)};
        writeBytes(os, temp);
    }

    public static void writeInt4(OutputStream os, int n) {
        byte[] temp = new byte[4];
        writeInt4tobytes(n, temp, 0);
        writeBytes(os, temp);
    }

    public static void writeInt2tobytes(int n, byte[] b, int offset) {
        b[offset] = (byte) ((n >> 8) & 0xff);
        b[offset + 1] = (byte) (n & 0xff);
    }

    public static void writeInt4tobytes(int n, byte[] b, int offset) {
        b[offset] = (byte) ((n >> 24) & 0xff);
        b[offset + 1] = (byte) ((n >> 16) & 0xff);
        b[offset + 2] = (byte) ((n >> 8) & 0xff);
        b[offset + 3] = (byte) (n & 0xff);
    }

    /**
     * guaranteed to read exactly len bytes. throws error if it can't
     */
    public static void readBytes(InputStream is, byte[] b, int offset, int len) {
		if (len == 0) {
			return;
		}
        try {
            int read = 0;
            while (read < len) {
                int n = is.read(b, offset + read, len - read);
				if (n < 1) {
					throw new PngjInputException("error reading bytes, " + n + " !=" + len);
				}
                read += n;
            }
        } catch (IOException e) {
            throw new PngjInputException("error reading", e);
        }
    }

    public static void skipBytes(InputStream is, long len) {
        try {
            while (len > 0) {
                long n1 = is.skip(len);
                if (n1 > 0) {
                    len -= n1;
                } else { // should we retry? lets read one byte
					if (is.read() == -1) // EOF
					{
						break;
					} else {
						len--;
					}
				}
            }
        } catch (IOException e) {
            throw new PngjInputException(e);
        }
    }

    public static void writeBytes(OutputStream os, byte[] b) {
        try {
            os.write(b);
        } catch (IOException e) {
            throw new PngjOutputException(e);
        }
    }

    public static void writeBytes(OutputStream os, byte[] b, int offset, int n) {
        try {
            os.write(b, offset, n);
        } catch (IOException e) {
            throw new PngjOutputException(e);
        }
    }

    public static int filterRowPaeth(int r, int left, int up, int upleft) { // a = left, b = above, c
        // = upper left
        return (r - filterPaethPredictor(left, up, upleft)) & 0xFF;
    }

    static int filterPaethPredictor(final int a, final int b, final int c) { // a = left, b =
        // above, c = upper
        // left
        // from
        // http://www.libpng.org/pub/png/spec/1.2/PNG-Filters.html

        final int p = a + b - c;// ; initial estimate
        final int pa = p >= a ? p - a : a - p;
        final int pb = p >= b ? p - b : b - p;
        final int pc = p >= c ? p - c : c - p;
        // ; return nearest of a,b,c,
        // ; breaking ties in order a,b,c.
		if (pa <= pb && pa <= pc) {
			return a;
		} else if (pb <= pc) {
			return b;
		} else {
			return c;
		}
    }

    public static InputStream istreamFromFile(File f) {
        FileInputStream is;
        try {
            is = new FileInputStream(f);
        } catch (Exception e) {
            throw new PngjInputException("Could not open " + f, e);
        }
        return is;
    }

    static OutputStream ostreamFromFile(File f, boolean allowoverwrite) {
        // In old versions of GAE (Google App Engine) this could trigger
        // issues because java.io.FileOutputStream was not whitelisted.
        java.io.FileOutputStream os = null;
		if (f.exists() && !allowoverwrite) {
			throw new PngjOutputException("File already exists: " + f);
		}
        try {
            os = new java.io.FileOutputStream(f);
        } catch (Exception e) {
            throw new PngjInputException("Could not open for write" + f, e);
        }
        return os;
    }

    public static long getDigest(PngReader pngr) {
        return pngr.getSimpleDigest();
    }
}