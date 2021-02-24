package org.xbib.graphics.ghostscript.internal;

public interface ErrorCodes {
    int gs_error_ok = 0;
    int gs_error_unknownerror = -1;    /* unknown error */
    int gs_error_dictfull = -2;
    int gs_error_dictstackoverflow = -3;
    int gs_error_dictstackunderflow = -4;
    int gs_error_execstackoverflow = -5;
    int gs_error_interrupt = -6;
    int gs_error_invalidaccess = -7;
    int gs_error_invalidexit = -8;
    int gs_error_invalidfileaccess = -9;
    int gs_error_invalidfont = -10;
    int gs_error_invalidrestore = -11;
    int gs_error_ioerror = -12;
    int gs_error_limitcheck = -13;
    int gs_error_nocurrentpoint = -14;
    int gs_error_rangecheck = -15;
    int gs_error_stackoverflow = -16;
    int gs_error_stackunderflow = -17;
    int gs_error_syntaxerror = -18;
    int gs_error_timeout = -19;
    int gs_error_typecheck = -20;
    int gs_error_undefined = -21;
    int gs_error_undefinedfilename = -22;
    int gs_error_undefinedresult = -23;
    int gs_error_unmatchedmark = -24;
    int gs_error_VMerror = -25;        /* must be the last Level 1 error */

    /* ------ Additional Level 2 errors (also in DPS, ------ */

    int gs_error_configurationerror = -26;
    int gs_error_undefinedresource = -27;

    int gs_error_unregistered = -28;
    int gs_error_invalidcontext = -29;

    /* invalidid is for the NeXT DPS extension. */

    int gs_error_invalidid = -30;

    /* ------ Pseudo-errors used internally ------ */

    int gs_error_hit_detected = -99;

    int gs_error_Fatal = -100;
    /*
     * Internal code for the .quit operator.
     * The real quit code is an integer on the operand stack.
     * gs_interpret returns this only for a .quit with a zero exit code.
     */
    int gs_error_Quit = -101;

    /*
     * Internal code for a normal exit from the interpreter.
     * Do not use outside of interp.c.
     */
    int gs_error_InterpreterExit = -102;

    /* Need the remap color error for high level pattern support */
    int gs_error_Remap_Color = -103;

    /*
     * Internal code to indicate we have underflowed the top block
     * of the e-stack.
     */
    int gs_error_ExecStackUnderflow = -104;

    /*
     * Internal code for the vmreclaim operator with a positive operand.
     * We need to handle this as an error because otherwise the interpreter
     * won't reload enough of its state when the operator returns.
     */
    int gs_error_VMreclaim = -105;

    /*
     * Internal code for requesting more input from run_string.
     */
    int gs_error_NeedInput = -106;

    /*
     * Internal code for a normal exit when usage info is displayed.
     * This allows Window versions of Ghostscript to pause until
     * the message can be read.
     */
    int gs_error_Info = -110;

    /* A special 'error', like reamp color above. This is used by a subclassing
     * device to indicate that it has fully processed a device method, and parent
     * subclasses should not perform any further action. Currently this is limited
     * to compositor creation.
     */
    int gs_error_handled = -111;
}
