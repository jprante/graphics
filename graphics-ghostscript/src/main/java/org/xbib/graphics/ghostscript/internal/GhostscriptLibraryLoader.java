package org.xbib.graphics.ghostscript.internal;

import com.sun.jna.Function;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GhostscriptLibraryLoader {

    private static final Logger logger = Logger.getLogger(GhostscriptLibraryLoader.class.getName());

    private static final String[] LIBNAMES = {
            "libgs.so.9.25", // RHEL 8
            "libgs.9.25.dylib", // macOS
            "gs.9.25" // other
    };

    public static GhostscriptLibrary loadLibrary() {
        Map<String, Object> options = new HashMap<>();
        options.put(Library.OPTION_CALLING_CONVENTION, Function.C_CONVENTION);
        for (String libname : LIBNAMES) {
            try {
                return Native.load(libname, GhostscriptLibrary.class, options);
            } catch (Error e) {
                logger.log(Level.WARNING, "library " + libname + " not found");
            }
        }
        return null;
    }
}
