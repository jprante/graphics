package org.xbib.graphics.ghostscript.test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xbib.graphics.ghostscript.Ghostscript;
import org.xbib.graphics.ghostscript.GhostscriptRevision;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

public class GhostscriptTest {

    private static final String dir = "src/test/resources/org/xbib/graphics/ghostscript/test/";

    private static Ghostscript gs;

    @BeforeAll
    public static void setup() throws IOException {
        gs = Ghostscript.getInstance();
    }

    @AfterAll
    public static void down() throws IOException {
        Ghostscript.deleteInstance();
    }

    @Test
    public void testGetRevision() {
        GhostscriptRevision revision = Ghostscript.getRevision();
        assertNotNull(revision.getProduct());
        assertNotNull(revision.getCopyright());
        assertNotNull(revision.getRevisionDate());
        assertNotNull(revision.getNumber());
    }

    @Test
    public void testExit() throws IOException {
        String[] args = { "-dNODISPLAY", "-dQUIET" };
        gs.initialize(args);
        gs.exit();
    }

    @Test
    public void testRunString() throws IOException {
        String[] args = { "-dNODISPLAY", "-dQUIET" };
        gs.initialize(args);
        gs.runString("devicenames ==");
        gs.exit();
    }

    @Test
    public void testRunFile() throws IOException {
        String[] args = { "-dNODISPLAY", "-dQUIET", "-dNOPAUSE", "-dBATCH", "-dSAFER"};
        gs.initialize(args);
        gs.runFile(dir + "input.ps");
        gs.exit();
    }

    // core dum p[libgs.so.9.25+0x32dc11]  clump_splay_walk_fwd+0x31
    //
    @Disabled
    @Test
    public void testStdIn() throws IOException {
        InputStream is = new FileInputStream(dir + "input.ps");
        gs.setStdIn(is);
        String[] args = { "-dNODISPLAY", "-dQUIET", "-dNOPAUSE", "-dBATCH", "-sOutputFile=%stdout", "-f", "-"};
        gs.initialize(args);
        gs.exit();
        is.close();
    }

    @Test
    public void testStdOut() throws IOException {
        InputStream is = new ByteArrayInputStream("devicenames ==\n".getBytes());
        gs.setStdIn(is);
        String[] args = { "-dNODISPLAY", "-sOutputFile=%stdout", "-f", "-"};
        gs.initialize(args);
        gs.exit();
        is.close();
    }

    @Test
    public void testStdErr() throws IOException {
        try {
            InputStream is = new ByteArrayInputStream("stupid\n".getBytes());
            gs.setStdIn(is);
            String[] args = { "-dNODISPLAY", "-sOutputFile=%stdout", "-f", "-"};
            gs.initialize(args);
            gs.exit();
            is.close();
        } catch (Exception e) {
            if (!e.getMessage().contains("error code -100")) {
                fail(e.getMessage());
            }
        }
    }
}
