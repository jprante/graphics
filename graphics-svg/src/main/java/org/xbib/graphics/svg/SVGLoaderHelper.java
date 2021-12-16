package org.xbib.graphics.svg;

import java.net.URI;

public class SVGLoaderHelper
{
    public final SVGUniverse universe;

    public final SVGDiagram diagram;

    public final URI xmlBase;

    public SVGLoaderHelper(URI xmlBase, SVGUniverse universe, SVGDiagram diagram)
    {
        this.xmlBase = xmlBase;
        this.universe = universe;
        this.diagram = diagram;
    }

}
