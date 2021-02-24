package org.xbib.graphics.io.vector.eps;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.io.vector.ProcessorResult;
import org.xbib.graphics.io.vector.Command;
import org.xbib.graphics.io.vector.PageSize;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class EPSProcessorTest {

    private static final String EOL = "\n";

    private static final Object[] HEADER = {
            "%!PS-Adobe-3.0 EPSF-3.0",
            "%%BoundingBox: 0 28 57 114",
            "%%HiResBoundingBox: 0.0 28.34645669291339 56.69291338582678 113.38582677165356",
            "%%LanguageLevel: 3",
            "%%Pages: 1",
            "%%EndComments",
            "%%Page: 1 1",
            "/M /moveto load def",
            "/L /lineto load def",
            "/C /curveto load def",
            "/Z /closepath load def",
            "/RL /rlineto load def",
            "/rgb /setrgbcolor load def",
            "/rect { /height exch def /width exch def /y exch def /x exch def x y M width 0 RL 0 height RL width neg 0 RL } bind def",
            "/ellipse { /endangle exch def /startangle exch def /ry exch def /rx exch def /y exch def /x exch def /savematrix matrix currentmatrix def x y translate rx ry scale 0 0 1 startangle endangle arcn savematrix setmatrix } bind def",
            "/imgdict { /datastream exch def /hasdata exch def /decodeScale exch def /bits exch def /bands exch def /imgheight exch def /imgwidth exch def << /ImageType 1 /Width imgwidth /Height imgheight /BitsPerComponent bits /Decode [bands {0 decodeScale} repeat] ",
            "/ImageMatrix [imgwidth 0 0 imgheight 0 0] hasdata { /DataSource datastream } if >> } bind def",
            "/latinize { /fontName exch def /fontNameNew exch def fontName findfont 0 dict copy begin /Encoding ISOLatin1Encoding def fontNameNew /FontName def currentdict end dup /FID undef fontNameNew exch definefont pop } bind def",
            Pattern.compile("/\\S+?Lat /\\S+ latinize /\\S+?Lat 12.0 selectfont"),
            "gsave",
            "clipsave",
            "/DeviceRGB setcolorspace",
            "0 85.03937007874016 translate",
            "2.834645669291339 -2.834645669291339 scale",
            "/basematrix matrix currentmatrix def",
            "%%EOF"
    };
    private static final PageSize PAGE_SIZE = new PageSize(0.0, 10.0, 20.0, 30.0);

    private final EPSProcessor processor = new EPSProcessor();

    private final List<Command<?>> commands = new LinkedList<>();

    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    private String process(Command<?>... commands) throws IOException {
        Collections.addAll(this.commands, commands);
        ProcessorResult processed = processor.process(this.commands, PAGE_SIZE);
        processed.write(bytes);
        return bytes.toString(StandardCharsets.ISO_8859_1);
    }

    @Test
    public void envelopeForEmptyDocument() throws IOException {
        String result = process();
        TestUtils.Template actual = new TestUtils.Template(result.split(EOL));
        TestUtils.Template expected = new TestUtils.Template(HEADER);
        TestUtils.assertTemplateEquals(expected, actual);
    }
}
