package org.xbib.graphics.svg.test;

import org.junit.jupiter.api.Test;
import org.xbib.graphics.svg.SVGDiagram;
import org.xbib.graphics.svg.SVGUniverse;

import java.io.IOException;
import java.io.InputStream;

public class SimpleSVGLoadTest {

    @Test
    public void test() throws IOException {
        InputStream inputStream = getClass().getResourceAsStream("test.svg");
        SVGUniverse svgUniverse = new SVGUniverse();
        SVGDiagram diagram = svgUniverse.getDiagram(svgUniverse.loadSVG(inputStream, "test.svg"));
    }
}
