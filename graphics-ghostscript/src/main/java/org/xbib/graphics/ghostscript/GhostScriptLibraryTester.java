package org.xbib.graphics.ghostscript;

import com.sun.jna.ptr.IntByReference;
import org.xbib.graphics.ghostscript.internal.GhostscriptLibrary;
import org.xbib.graphics.ghostscript.internal.GhostscriptLibraryLoader;

public class GhostScriptLibraryTester {

    private final GhostscriptLibrary ghostscriptLibrary;

    public GhostScriptLibraryTester() {
        ghostscriptLibrary = GhostscriptLibraryLoader.loadLibrary();
    }

    public String getRevisionProduct() {
        GhostscriptLibrary.gsapi_revision_s revision = new GhostscriptLibrary.gsapi_revision_s();
        ghostscriptLibrary.gsapi_revision(revision, revision.size());
        return revision.product;
    }

    public int createInstance() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        int result = ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        if (result == 0) {
            result = ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
            ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        }
        return result;
    }

    public int runString() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        String[] args = {
                "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        IntByReference exitCode = new IntByReference();
        ghostscriptLibrary.gsapi_run_string(instanceByRef.getValue(), "devicenames ==\n", 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return exitCode.getValue();
    }

    public int withInput(String input, String output) {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        String[] args = {
                "ps2pdf",
                "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER", "-sDEVICE=pdfwrite",
                "-sOutputFile=" + output,
                "-c", ".setpdfwrite", "-f", input
        };
        int result = ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return result;
    }

    public int runStringWithLength() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        String[] args = {
                "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        IntByReference exitCode = new IntByReference();
        String str = "devicenames ==\n";
        ghostscriptLibrary.gsapi_run_string_with_length(instanceByRef.getValue(), str, str.length(), 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return exitCode.getValue();
    }

    public int runStringContinue() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        String[] args = {
                "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        IntByReference exitCode = new IntByReference();
        ghostscriptLibrary.gsapi_run_string_begin(instanceByRef.getValue(), 0, exitCode);
        String str = "devicenames ==\n";
        ghostscriptLibrary.gsapi_run_string_continue(instanceByRef.getValue(), str, str.length(), 0, exitCode);
        ghostscriptLibrary.gsapi_run_string_end(instanceByRef.getValue(), 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return exitCode.getValue();
    }

    public int runFile(String input) {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        String[] args = {
                "-dNODISPLAY", "-dNOPAUSE", "-dBATCH", "-dSAFER"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        IntByReference exitCode = new IntByReference();
        ghostscriptLibrary.gsapi_run_file(instanceByRef.getValue(), input, 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return exitCode.getValue();
    }

    public void setStdio() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        final StringBuilder stdOutBuffer = new StringBuilder();
        final StringBuilder stdInBuffer = new StringBuilder();
        GhostscriptLibrary.stdin_fn stdinCallback = (caller_handle, buf, len) -> {
            stdInBuffer.append("OK");
            return 0;
        };
        GhostscriptLibrary.stdout_fn stdoutCallback = (caller_handle, str, len) -> {
            stdOutBuffer.append(str, 0, len);
            return len;
        };
        GhostscriptLibrary.stderr_fn stderrCallback = (caller_handle, str, len) -> len;
        ghostscriptLibrary.gsapi_set_stdio(instanceByRef.getValue(), stdinCallback, stdoutCallback, stderrCallback);
        String[] args = {
                "-dNODISPLAY", "-dQUIET", "-dNOPAUSE", "-dBATCH",
                "-sOutputFile=%stdout", "-f", "-"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(),
                args.length, args);
        IntByReference exitCode = new IntByReference();
        String command = "devicenames ==\n";
        ghostscriptLibrary.gsapi_run_string_with_length(instanceByRef.getValue(), command, command.length(), 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
    }

    public String setDisplayCallback() {
        GhostscriptLibrary.gs_main_instance.ByReference instanceByRef = new GhostscriptLibrary.gs_main_instance.ByReference();
        ghostscriptLibrary.gsapi_new_instance(instanceByRef.getPointer(), null);
        final StringBuilder result = new StringBuilder();
        GhostscriptLibrary.display_callback_s displayCallback = new GhostscriptLibrary.display_callback_s();
        displayCallback.version_major = 2;
        displayCallback.version_minor = 0;
        displayCallback.display_open = (handle, device) -> {
            result.append("OPEN-");
            return 0;
        };
        displayCallback.display_preclose = (handle, device) -> {
            result.append("PRECLOSE-");
            return 0;
        };
        displayCallback.display_close = (handle, device) -> {
            result.append("CLOSE");
            return 0;
        };
        displayCallback.display_presize = (handle, device, width, height, raster, format) -> {
            result.append("PRESIZE-");
            return 0;
        };
        displayCallback.display_size = (handle, device, width, height, raster, format, pimage) -> {
            result.append("SIZE-");
            return 0;
        };
        displayCallback.display_sync = (handle, device) -> {
            result.append("SYNC-");
            return 0;
        };
        displayCallback.display_page = (handle, device, copies, flush) -> {
            result.append("PAGE-");
            return 0;
        };
        displayCallback.display_update = (handle, device, x, y, w, h) -> {
            result.append("UPDATE-");
            return 0;
        };
        displayCallback.display_memalloc = null;
        displayCallback.display_memfree = null;
        displayCallback.size = displayCallback.size();
        ghostscriptLibrary.gsapi_set_display_callback(instanceByRef.getValue(), displayCallback);
        String[] args = new String[]{
                "-dQUIET", "-dNOPAUSE", "-dBATCH", "-dSAFER",
                "-sDEVICE=display", "-sDisplayHandle=0", "-dDisplayFormat=16#a0800"
        };
        ghostscriptLibrary.gsapi_init_with_args(instanceByRef.getValue(), args.length, args);
        IntByReference exitCode = new IntByReference();
        String command = "showpage\n";
        ghostscriptLibrary.gsapi_run_string_with_length(instanceByRef.getValue(), command, command.length(), 0, exitCode);
        ghostscriptLibrary.gsapi_exit(instanceByRef.getValue());
        ghostscriptLibrary.gsapi_delete_instance(instanceByRef.getValue());
        return result.toString();
    }
}
