package org.xbib.graphics.chart.io.vector.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class Base64EncodeStream extends FilterOutputStream {
    private static final int BASE = 64;
    private static final int[] POW_64 =
            {BASE * BASE * BASE, BASE * BASE, BASE, 1};
    private static final char[] CHAR_MAP =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
                    .toCharArray();
    private final byte[] data;
    private final byte[] encoded;
    private boolean closed;
    private int dataSize;

    public Base64EncodeStream(OutputStream out) {
        super(out);
        data = new byte[3];
        encoded = new byte[4];
    }

    private static long toUInt32(byte[] bytes, int size) {
        long uint32 = 0L;
        int offset = (3 - size) * 8;
        for (int i = size - 1; i >= 0; i--) {
            uint32 |= (bytes[i] & 0xff) << offset;
            offset += 8;
        }
        return toUnsignedInt(uint32);
    }

    private static long toUnsignedInt(long x) {
        return x & 0x00000000ffffffffL;
    }

    @Override
    public void write(int b) throws IOException {
        if (closed) {
            return;
        }
        if (dataSize == data.length) {
            writeChunk();
            dataSize = 0;
        }
        data[dataSize++] = (byte) (b & 0xff);
    }

    private void writeChunk() throws IOException {
        if (dataSize == 0) {
            return;
        }
        long uint32 = toUInt32(data, dataSize);
        int padByteCount = data.length - dataSize;
        int encodedSize = encodeChunk(uint32, padByteCount);
        out.write(encoded, 0, encodedSize);
    }

    private int encodeChunk(long uint32, int padByteCount) {
        Arrays.fill(encoded, (byte) '=');
        int size = encoded.length - padByteCount;
        for (int i = 0; i < size; i++) {
            encoded[i] = (byte) CHAR_MAP[(int) (uint32 / POW_64[i] % BASE)];
        }
        return encoded.length;
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }

        writeChunk();

        super.close();
        closed = true;
    }
}

