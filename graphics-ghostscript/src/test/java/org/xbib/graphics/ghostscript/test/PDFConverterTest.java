package org.xbib.graphics.ghostscript.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.PDFConverter;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PDFConverterTest {

    @Test
    @Disabled
    public void testConvertWithPS() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDFConverter converter = new PDFConverter();
        converter.convert(this.getClass().getClassLoader().getResourceAsStream("input.ps"), baos);
        assertTrue(baos.size() > 0);
        baos.close();
    }

    @Test
    @Disabled
    public void testConvertWithPSMultiProcess() throws Exception {
        final ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        final ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        final ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
        final PDFConverter converter = new PDFConverter();
        Thread thread1 = new Thread() {
            public void run() {
                try {
                    converter.convert(this.getClass().getClassLoader().getResourceAsStream("input.ps"), baos1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        Thread thread2 = new Thread() {
			public void run() {
				try {
					converter.convert(this.getClass().getClassLoader().getResourceAsStream("input.ps"), baos2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		thread2.start();
		Thread thread3 = new Thread() {
			public void run() {
				try {
					converter.convert(this.getClass().getClassLoader().getResourceAsStream("input.ps"), baos3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		};
		thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        assertTrue(baos1.size() > 0);
        baos1.close();
        assertTrue(baos2.size() > 0);
        baos2.close();
        assertTrue(baos3.size() > 0);
        baos3.close();
    }

    @Test
    public void testConvertWithUnsupportedDocument() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PDFConverter converter = new PDFConverter();
        converter.convert(this.getClass().getClassLoader().getResourceAsStream("input.pdf"), baos);
        baos.close();
    }
}
