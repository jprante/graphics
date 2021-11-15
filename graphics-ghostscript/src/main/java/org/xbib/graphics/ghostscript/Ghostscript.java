package org.xbib.graphics.ghostscript;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import org.xbib.graphics.ghostscript.internal.ErrorCodes;
import org.xbib.graphics.ghostscript.internal.GhostscriptLibrary;
import org.xbib.graphics.ghostscript.internal.GhostscriptLibraryLoader;
import org.xbib.graphics.ghostscript.internal.LoggingOutputStream;
import org.xbib.graphics.ghostscript.internal.NullOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Ghostscript native library API.
 */
public class Ghostscript {

    private static final Logger logger = Logger.getLogger(Ghostscript.class.getName());

    public static final String ENCODING_PARAMETER = "org.xbib.graphics.ghostscript.encoding";

    private static Ghostscript instance;

    private static GhostscriptLibrary libraryInstance;

    private static GhostscriptLibrary.gs_main_instance.ByReference nativeInstanceByRef;

    private static InputStream stdIn;

    private static OutputStream stdOut;

    private static OutputStream stdErr;

    private static Path tmpDir;

    private Ghostscript() {
    }

    public static synchronized Ghostscript getInstance() throws IOException {
        if (instance == null) {
            prepareTmp();
            instance = new Ghostscript();
            libraryInstance = getGhostscriptLibrary();
            nativeInstanceByRef = getNativeInstanceByRef();
            stdOut = new LoggingOutputStream(logger);
            stdErr = new LoggingOutputStream(logger);
            instance.setStdOut(stdOut);
            instance.setStdErr(stdErr);
        }
        return instance;
    }

    private static synchronized GhostscriptLibrary getGhostscriptLibrary() {
        if (libraryInstance == null) {
            libraryInstance = GhostscriptLibraryLoader.loadLibrary();
        }
        return libraryInstance;
    }

    private static synchronized GhostscriptLibrary.gs_main_instance.ByReference getNativeInstanceByRef() throws IOException {
        if (nativeInstanceByRef == null) {
            nativeInstanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
            int result = libraryInstance.gsapi_new_instance(nativeInstanceByRef.getPointer(), null);
            if (result != 0) {
                nativeInstanceByRef = null;
                throw new IOException("can not get Ghostscript instance, error code " + result);
            }
        }
        return nativeInstanceByRef;
    }

    /**
     * Deletes the singleton instance of the Ghostscript object. This ensures
     * that the native Ghostscript interpreter instance is deleted. This method
     * must be called if Ghostscript is not used anymore.
     * @throws IOException if delete of instance fails
     */
    public static synchronized void deleteInstance() throws IOException {
        if (instance != null) {
            if (libraryInstance != null) {
                libraryInstance.gsapi_delete_instance(nativeInstanceByRef.getValue());
                libraryInstance = null;
            }
            if (nativeInstanceByRef != null) {
                nativeInstanceByRef = null;
            }
            instance = null;
        }
    }

    /**
     * Gets Ghostscript revision data.
     *
     * @return the Ghostscript revision data.
     */
    public static GhostscriptRevision getRevision() {
        getGhostscriptLibrary();
        GhostscriptLibrary.gsapi_revision_s revision = new GhostscriptLibrary.gsapi_revision_s();
        libraryInstance.gsapi_revision(revision, revision.size());
        GhostscriptRevision result = new GhostscriptRevision();
        result.setProduct(revision.product);
        result.setCopyright(revision.copyright);
        result.setNumber(Float.toString(revision.revision.floatValue() / 100));
        result.setRevisionDate(LocalDate.parse(revision.revisiondate.toString(),
                DateTimeFormatter.ofPattern("yyyyMMdd")) );
        return result;
    }

    /**
     * Gets the error output stream of the Ghostscript interpreter (may be null
     * if not set).
     *
     * @return The OutputStream or null
     */
    public synchronized OutputStream getStdErr() {
        return stdErr;
    }

    /**
     * Sets the error output stream of the Ghostscript interpreter.
     *
     * @param outputStream OutputStream object
     */
    public synchronized void setStdErr(OutputStream outputStream) {
        stdErr = outputStream;
    }

    /**
     * Gets the standard output stream of the Ghostscript interpreter.
     *
     * @return The OutputStream or null
     */
    public synchronized OutputStream getStdOut() {
        return stdOut;
    }

    /**
     * Sets the standard output stream of the Ghostscript interpreter.
     *
     * @param outputStream OutputStream object
     */
    public synchronized void setStdOut(OutputStream outputStream) {
        stdOut = outputStream;
    }

    /**
     * Gets the standard input stream of the Ghostscript interpreter.
     *
     * @return The InputStream or null
     */
    public synchronized InputStream getStdIn() {
        return stdIn;
    }

    /**
     * Sets the standard input stream of the Ghostscript interpreter.
     *
     * @param inputStream InputStream object
     */
    public synchronized void setStdIn(InputStream inputStream) {
        stdIn = inputStream;
    }

    /**
     * Initializes Ghostscript interpreter.
     *
     * @param args Interpreter parameters. Use the same as Ghostscript command
     *             line arguments.
     * @throws IOException if initialize fails
     */
    public void initialize(String[] args) throws IOException {
        getGhostscriptLibrary();
        getNativeInstanceByRef();
        int result;
        GhostscriptLibrary.stdin_fn stdinCallback = null;
        if (getStdIn() != null) {
            stdinCallback = (caller_handle, buf, len) -> {
                String encoding = System.getProperty(ENCODING_PARAMETER, "UTF-8");
                try {
                    byte[] buffer = new byte[1024];
                    int read = getStdIn().read(buffer);
                    if (read != -1) {
                        buf.setString(0, new String(buffer, 0, read, encoding));
                        return read;
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, e.getMessage(), e);
                }
                return 0;
            };
        }
        GhostscriptLibrary.stdout_fn stdoutCallback;
        if (getStdOut() == null) {
            setStdOut(new NullOutputStream());
        }
        stdoutCallback = (caller_handle, str, len) -> {
            try {
                getStdOut().write(str.getBytes(), 0, len);
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            return len;
        };
        GhostscriptLibrary.stderr_fn stderrCallback;
        if (getStdErr() == null) {
            setStdErr(new NullOutputStream());
        }
        stderrCallback = (caller_handle, str, len) -> {
            try {
                getStdErr().write(str.getBytes(), 0, len);
            } catch (IOException ex) {
               logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            return len;
        };
        logger.log(Level.FINE, "setting gsapi_set_stdio");
        result = libraryInstance.gsapi_set_stdio(nativeInstanceByRef.getValue(), stdinCallback,
                stdoutCallback, stderrCallback);
        if (result != 0) {
            throw new IOException("can not set stdio on Ghostscript interpreter, error code " + result);
        }
        logger.log(Level.FINE, "gsapi_init_with_args = " + (args != null ? Arrays.asList(args) : "null"));
        result = libraryInstance.gsapi_init_with_args(nativeInstanceByRef.getValue(),
                args != null ? args.length : 0, args);
        logger.log(Level.FINE, "gsapi_init_with_args result = " + result);
        if (result == ErrorCodes.gs_error_Quit) {
            result = 0;
        }
        if (result == 0) {
            return;
        }
        if (result < 0) {
            exit();
            throw new IOException("can not initialize Ghostscript interpreter, error code " + result);
        }
    }

    /**
     * Exits Ghostscript interpreter.
     *
     * @throws IOException if exit fails
     */
    public void exit() throws IOException {
        getGhostscriptLibrary();
        getNativeInstanceByRef();
        Pointer pointer = nativeInstanceByRef.getValue();
        if (pointer != null) {
            int result = libraryInstance.gsapi_exit(pointer);
            if (result != 0) {
                throw new IOException("can not exit Ghostscript interpreter, error code " + result);
            }
        }
    }

    /**
     * Sends command string to Ghostscript interpreter.
     * Must be called after initialize method.
     *
     * @param string Command string
     * @throws IOException if run fails
     */
    public void runString(String string) throws IOException {
        getGhostscriptLibrary();
        getNativeInstanceByRef();
        IntByReference exitCode = new IntByReference();
        libraryInstance.gsapi_run_string_begin(nativeInstanceByRef.getValue(), 0, exitCode);
        if (exitCode.getValue() != 0) {
            throw new IOException("can not run command on Ghostscript interpreter. gsapi_run_string_begin failed with error code "
                    + exitCode.getValue());
        }
        String[] slices = string.split("\n");
        for (String slice1 : slices) {
            String slice = slice1 + "\n";
            libraryInstance.gsapi_run_string_continue(nativeInstanceByRef.getValue(), slice, slice.length(),
                    0, exitCode);
            if (exitCode.getValue() != 0) {
                throw new IOException("can not run command on Ghostscript interpreter. gsapi_run_string_continue failed with error code "
                        + exitCode.getValue());
            }
        }
        libraryInstance.gsapi_run_string_end(nativeInstanceByRef.getValue(), 0, exitCode);
        if (exitCode.getValue() != 0) {
            throw new IOException("can not run command on Ghostscript interpreter. gsapi_run_string_end failed with error code "
                    + exitCode.getValue());
        }
    }

    /**
     * Sends postscript file to Ghostscript interpreter. Must be called after initialize
     * method.
     *
     * @param fileName File name
     * @throws IOException if run of file fails
     */
    public void runFile(String fileName) throws IOException {
        getGhostscriptLibrary();
        getNativeInstanceByRef();
        IntByReference exitCode = new IntByReference();
        libraryInstance.gsapi_run_file(nativeInstanceByRef.getValue(), fileName, 0, exitCode);
        if (exitCode.getValue() != 0) {
            throw new IOException("can not run file on Ghostscript interpreter, error code " + exitCode.getValue());
        }
    }

    private static void prepareTmp() throws IOException {
        String tmp = System.getenv("TMPDIR"); // the variable that ghostscript uses
        if (tmp == null) {
            tmp = System.getenv("TEMP");
        }
        if (tmp == null) {
            throw new IllegalStateException("no TEMP/TMPDIR environment set for ghostscript");
        }
        tmpDir = Paths.get(tmp);
        if (!Files.exists(tmpDir)) {
            Files.createDirectories(tmpDir);
        }
        // never delete TMPDIR or TEMP automatically, it might be a shared directory
    }
}
