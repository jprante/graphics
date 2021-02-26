package org.xbib.graphics.ghostscript.internal;

import static com.sun.jna.Platform.LINUX;
import static com.sun.jna.Platform.MAC;
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

    private static final String[] LINUX_LIBNAMES = {
            "gs.so.9.27",
            "gs.9.27",
            "gs.so.9.25",
            "gs.9.25",
            "gs"
    };

    private static final String[] MAC_LIBNAMES = {
            "libgs.9.25.dylib",
            "libgs.9.25",
            "gs.9.25",
            "libgs.dylib",
            "libgs",
            "gs",
    };

    public static GhostscriptLibrary loadLibrary() {
        Map<String, Object> options = new HashMap<>();
        options.put(Library.OPTION_CALLING_CONVENTION, Function.C_CONVENTION);
        String[] LIBNAMES = {};
        switch (Platform.getOSType()) {
            case LINUX:
                LIBNAMES = LINUX_LIBNAMES;
                break;
            case MAC:
                LIBNAMES = MAC_LIBNAMES;
                break;
        }
        for (String libname : LIBNAMES) {
            try {
                return Native.load(libname, GhostscriptLibrary.class, options);
            } catch (Error e) {
                logger.log(Level.WARNING, "library " + libname + " not found", e);
            }
        }
        return null;
    }
}
