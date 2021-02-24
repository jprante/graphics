package org.xbib.graphics.ghostscript.test;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.GhostScriptLibraryTester;

import java.io.File;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GhostscriptLibraryTest {

    private static final Logger logger = Logger.getLogger(GhostscriptLibraryTest.class.getName());

    private static final String dir = "src/test/resources/org/xbib/graphics/ghostscript/test/";

    private static GhostScriptLibraryTester gslib;

    @BeforeAll
    public static void setUp() throws Exception {
        gslib = new GhostScriptLibraryTester();
        logger.info("setUp: ghostscript library loaded");
    }

    @Test
    public void gsapiRevision() {
        assertTrue(gslib.getRevisionProduct().contains("Ghostscript"));
    }

    @Test
    public void gsapiInstance() {
        assertEquals(0, gslib.createInstance());
    }

    @Test
    @Disabled
    public void gsapiInitWithArgs() {
        String input = dir + "input.ps";
        String output = "build/output.pdf";
        gslib.withInput(input, output);
        File outputFile = new File(output);
        assertTrue(outputFile.exists());
    }

    @Test
    public void gsapiRunString() {
        gslib.runString();
    }

    @Test
    public void gsapiRunStringWithLength() {
        gslib.runStringWithLength();
    }

    @Test
    public void gsapiRunStringContinue() {
        gslib.runStringContinue();
    }

    @Test
    public void runFile() {
        gslib.runFile(dir + "input.ps");
    }

    @Test
    public void testSetStdio() {
        gslib.setStdio();
    }

    @Test
    @Disabled
    public void gsapiSetDisplayCallback() {
        String display = gslib.setDisplayCallback();
        assertEquals("OPEN-PRESIZE-UPDATE-SIZE-PAGE-UPDATE-SYNC-PRECLOSE-CLOSE", display);
    }
}
