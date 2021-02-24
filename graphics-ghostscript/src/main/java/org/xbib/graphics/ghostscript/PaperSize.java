package org.xbib.graphics.ghostscript;

/**
 * Defines paper size. Standard sizes are defined as constants.
 * @see <a href="http://ghostscript.com/doc/current/Use.htm#Known_paper_sizes">Ghostscript paper sizes</a>
 */
public enum PaperSize {

    A0(2384, 3370),
    A1(1684, 2384),
    A2(1191, 1684),
    A3(842, 1191),
    A4(595, 842),
    A5(420, 595),
    A6(297, 420),
    A7(210, 297),
    A8(148, 210),
    A9(105, 148),
    A10(73, 105),
    LEDGER(1224, 792),
    LEGAL(612, 1008),
    LETTER(612, 792),
    ARCHE(2592, 3456),
    ARCHD(1728, 2592),
    ARCHC(1296, 1728),
    ARCHB(864, 1296),
    ARCHA(648, 864);

    private final int width;
    private final int height;

    PaperSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /*public PaperSize scale(float factor) {
        return new PaperSize((int) (width * factor), (int) (height * factor));
    }*/

    /*public PaperSize portrait() {
        if (width > height) {
            return new PaperSize(height, width);
        } else {
            return new PaperSize(width, height);
        }
    }

    public PaperSize landscape() {
        if (width < height) {
            return new PaperSize(height, width);
        } else {
            return new PaperSize(width, height);
        }
    }*/

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
