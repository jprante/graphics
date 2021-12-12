package org.xbib.graphics.pdfbox.groovy.test

import groovy.util.logging.Log
import org.apache.fontbox.ttf.CmapSubtable
import org.apache.fontbox.ttf.NamingTable
import org.apache.fontbox.ttf.TTFParser
import org.apache.fontbox.ttf.TrueTypeFont
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths

@Log
class LoadFontTest {

    @Test
    void testOpenTypeFont() {
        def names = ['NotoSansCJKtc-Regular.ttf', 'NotoSansCJKtc-Bold.ttf']
        names.each { name ->
            InputStream inputStream = Files.newInputStream(Paths.get("src/test/resources/fonts/" + name))
            inputStream.withCloseable {
                addOpenTypeFont(name, inputStream)
            }
        }
    }

    private final Map<String, TrueTypeFont> ttf = new HashMap<>()
    private final Map<String, TrueTypeFont> otf = new HashMap<>()

    private void addOpenTypeFont(String name, InputStream inputStream) {
        TTFParser ttfParser = new TTFParser(false, true)
        TrueTypeFont trueTypeFont = ttfParser.parse(inputStream)
        try {
            NamingTable nameTable = trueTypeFont.getNaming()
            if (!nameTable) {
                log.warning("Missing 'name' table in font " + name)
            } else {
                if (nameTable.getPostScriptName()) {
                    String psName = nameTable.getPostScriptName()
                    String format
                    if (trueTypeFont.getTableMap().get("CFF ")) {
                        format = "OTF"
                        otf.put(psName, trueTypeFont);
                    } else {
                        format = "TTF";
                        ttf.put(psName, trueTypeFont)
                    }
                    log.info(format + ": '" + psName + "' / '" + nameTable.getFontFamily() +
                                "' / '" + nameTable.getFontSubFamily() + "'")
                } else {
                    log.warning("Missing 'name' entry for PostScript name in font " + inputStream)
                }
            }
            CmapSubtable cmapSubtable = trueTypeFont.getUnicodeCmap(true)
            if (!cmapSubtable) {
                log.warning('missing cmap table in ' + name)
            } else {
                log.info("cmap table present: " + name)
            }
        } finally {
            if (trueTypeFont != null) {
                trueTypeFont.close()
            }
        }
    }
}
