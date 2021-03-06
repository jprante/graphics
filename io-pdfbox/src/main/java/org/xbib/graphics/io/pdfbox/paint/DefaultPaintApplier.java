package org.xbib.graphics.io.pdfbox.paint;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionType3;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDPattern;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.pattern.PDTilingPattern;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShading;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType3;
import org.apache.pdfbox.pdmodel.graphics.shading.ShadingPaint;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.util.Matrix;
import org.xbib.graphics.io.pdfbox.image.ImageEncoder;
import org.xbib.graphics.io.pdfbox.PdfBoxGraphics2D;
import org.xbib.graphics.io.pdfbox.color.ColorMapper;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default paint applier.
 */
public class DefaultPaintApplier implements PaintApplier {

    private static final Logger logger = Logger.getLogger(DefaultPaintApplier.class.getName());

    private final ExtGStateCache extGStateCache = new ExtGStateCache();

    private final PDShadingCache shadingCache = new PDShadingCache();

    @Override
    public PDShading applyPaint(Paint paint, PDPageContentStream contentStream, AffineTransform tf,
                                PaintApplierEnvironment env) throws IOException {
        PaintApplierState state = new PaintApplierState();
        state.document = env.getDocument();
        state.resources = env.getResources();
        state.contentStream = contentStream;
        state.colorMapper = env.getColorMapper();
        state.imageEncoder = env.getImageEncoder();
        state.composite = env.getComposite();
        state.pdExtendedGraphicsState = null;
        state.paintApplierEnvironment = env;
        state.affineTransform = tf;
        state.nestedTransform = null;
        PDShading shading = applyPaint(paint, state);
        if (state.pdExtendedGraphicsState != null) {
            contentStream.setGraphicsStateParameters(extGStateCache.makeUnique(state.pdExtendedGraphicsState));
        }
        return shading;
    }

    @SuppressWarnings("WeakerAccess")
    protected void applyAsStrokingColor(Color color, PaintApplierState state) throws IOException {
        PDPageContentStream contentStream = state.contentStream;
        ColorMapper colorMapper = state.colorMapper;
        contentStream.setStrokingColor(colorMapper.mapColor(contentStream, color));
        contentStream.setNonStrokingColor(colorMapper.mapColor(contentStream, color));
        int alpha = color.getAlpha();
        if (alpha < 255) {
            state.ensureExtendedState();
            Float strokingAlphaConstant = state.pdExtendedGraphicsState.getStrokingAlphaConstant();
            if (strokingAlphaConstant == null) {
                strokingAlphaConstant = 1f;
            }
            state.pdExtendedGraphicsState.setStrokingAlphaConstant(strokingAlphaConstant * (alpha / 255f));
            Float nonStrokingAlphaConstant = state.pdExtendedGraphicsState.getNonStrokingAlphaConstant();
            if (nonStrokingAlphaConstant == null) {
                nonStrokingAlphaConstant = 1f;
            }
            state.pdExtendedGraphicsState.setNonStrokingAlphaConstant(nonStrokingAlphaConstant * (alpha / 255f));
        }
    }

    private PDShading applyPaint(Paint paint, PaintApplierState state) throws IOException {
        applyComposite(state);
        if (paint == null) {
            return null;
        }
        String simpleName = paint.getClass().getSimpleName();
        if (paint instanceof Color) {
            applyAsStrokingColor((Color) paint, state);
        } else if (simpleName.equals("LinearGradientPaint")) {
            return shadingCache.makeUnique(buildLinearGradientShading(paint, state));
        } else if (simpleName.equals("RadialGradientPaint")) {
            return shadingCache.makeUnique(buildRadialGradientShading(paint, state));
        } else if (simpleName.equals("PatternPaint")) {
            applyPatternPaint(paint, state);
        } else if (simpleName.equals("TilingPaint")) {
            logger.log(Level.WARNING, "no tiling paint available");
        } else if (paint instanceof GradientPaint) {
            return shadingCache.makeUnique(buildGradientShading((GradientPaint) paint, state));
        } else if (paint instanceof TexturePaint) {
            applyTexturePaint((TexturePaint) paint, state);
        } else if (paint instanceof ShadingPaint) {
            return shadingCache.makeUnique(importPDFBoxShadingPaint((ShadingPaint<?>) paint, state));
        } else {
            logger.log(Level.WARNING, "Don't know paint " + paint.getClass().getName());
        }
        return null;
    }

    private PDShading importPDFBoxShadingPaint(ShadingPaint<?> paint, PaintApplierState state)
            throws IOException {
        PDFCloneUtility pdfCloneUtility = new PDFCloneUtility(state.document);
        Matrix matrix = paint.getMatrix();
        PDShading shading = paint.getShading();
        state.contentStream.transform(matrix);
        return PDShading.create((COSDictionary) pdfCloneUtility
                .cloneForNewDocument(shading.getCOSObject()));
    }

    private void applyPatternPaint(Paint paint, PaintApplierState state) throws IOException {
        Rectangle2D anchorRect = getPropertyValue(paint, "getPatternRect");
        AffineTransform paintPatternTransform = getPropertyValue(paint, "getPatternTransform");
        PDTilingPattern pattern = new PDTilingPattern();
        pattern.setPaintType(PDTilingPattern.PAINT_COLORED);
        pattern.setTilingType(PDTilingPattern.TILING_CONSTANT_SPACING_FASTER_TILING);
        pattern.setBBox(new PDRectangle((float) anchorRect.getX(), (float) anchorRect.getY(),
                (float) anchorRect.getWidth(), (float) anchorRect.getHeight()));
        pattern.setXStep((float) anchorRect.getWidth());
        pattern.setYStep((float) anchorRect.getHeight());
        AffineTransform patternTransform = new AffineTransform();
        if (paintPatternTransform != null) {
            paintPatternTransform = new AffineTransform(paintPatternTransform);
            paintPatternTransform.preConcatenate(state.affineTransform);
            patternTransform.concatenate(paintPatternTransform);
        } else {
            patternTransform.concatenate(state.affineTransform);
        }
        patternTransform.scale(1f, -1f);
        pattern.setMatrix(patternTransform);
        PDAppearanceStream appearance = new PDAppearanceStream(state.document);
        appearance.setResources(pattern.getResources());
        appearance.setBBox(pattern.getBBox());
        Object graphicsNode = getPropertyValue(paint, "getGraphicsNode");
        PdfBoxGraphics2D pdfBoxGraphics2D =
                new PdfBoxGraphics2D(state.document, pattern.getBBox(), state.paintApplierEnvironment.getGraphics2D());
        try {
            Method paintMethod = graphicsNode.getClass().getMethod("paint", Graphics2D.class);
            paintMethod.invoke(graphicsNode, pdfBoxGraphics2D);
        } catch (Exception e) {
            logger.log(Level.WARNING, "PaintApplier error while drawing Batik PatternPaint " + e.getMessage());
            return;
        }
        pdfBoxGraphics2D.dispose();
        PDFormXObject xFormObject = pdfBoxGraphics2D.getXFormObject();
        PDPageContentStream imageContentStream = new PDPageContentStream(state.document, appearance,
                ((COSStream) pattern.getCOSObject()).createOutputStream());
        imageContentStream.drawForm(xFormObject);
        imageContentStream.close();
        PDColorSpace patternCS1 = new PDPattern(null);
        COSName tilingPatternName = state.resources.add(pattern);
        PDColor patternColor = new PDColor(tilingPatternName, patternCS1);
        state.contentStream.setNonStrokingColor(patternColor);
        state.contentStream.setStrokingColor(patternColor);
    }

    private void applyComposite(PaintApplierState state) {
        if (state.composite == null) {
            return;
        }
        float alpha = 1;
        COSName blendMode = COSName.COMPATIBLE;
        int rule = AlphaComposite.SRC;
        if (state.composite instanceof AlphaComposite) {
            AlphaComposite composite = (AlphaComposite) state.composite;
            alpha = composite.getAlpha();
            rule = composite.getRule();
        } else if (state.composite.getClass().getSimpleName().equals("SVGComposite")) {
            alpha = getPropertyValue(state.composite, "alpha");
            rule = getPropertyValue(state.composite, "rule");
        } else {
            logger.log(Level.WARNING, "Unknown composite " + state.composite.getClass().getSimpleName());
        }
        state.ensureExtendedState();
        if (alpha < 1) {
            state.pdExtendedGraphicsState.setStrokingAlphaConstant(alpha);
            state.pdExtendedGraphicsState.setNonStrokingAlphaConstant(alpha);
        }
        switch (rule) {
            case AlphaComposite.CLEAR:
            case AlphaComposite.DST_ATOP:
            case AlphaComposite.DST:
            case AlphaComposite.DST_IN:
            case AlphaComposite.DST_OUT:
            case AlphaComposite.SRC_IN:
            case AlphaComposite.SRC_OUT:
            case AlphaComposite.DST_OVER:
                break;
            case AlphaComposite.SRC:
                blendMode = COSName.NORMAL;
                break;
            case AlphaComposite.SRC_OVER:
            case AlphaComposite.SRC_ATOP:
                blendMode = COSName.COMPATIBLE;
                break;
            case AlphaComposite.XOR:
                blendMode = COSName.EXCLUSION;
                break;
        }
        state.dictExtendedState.setItem(COSName.BM, blendMode);
    }

    private Point2D clonePoint(Point2D point2D) {
        return new Point2D.Double(point2D.getX(), point2D.getY());
    }

    /**
     * Very small number, everything smaller than this is zero for us.
     */
    private static final double EPSILON = 0.00001;

    private PDShading buildLinearGradientShading(Paint paint, PaintApplierState state)
            throws IOException {
        /*
         * Batik has a copy of RadialGradientPaint, but it has the same structure as the AWT RadialGradientPaint. So we use
         * Reflection to access the fields of both these classes.
         */
        boolean isBatikGradient = paint.getClass().getPackage().getName()
                .equals("org.apache.batik.ext.awt");
        boolean isObjectBoundingBox = false;
        if (isBatikGradient) {
            AffineTransform gradientTransform = getPropertyValue(paint, "getTransform");
            if (!gradientTransform.isIdentity()) {
                /*
                 * If the scale is not square, we need to use the object bounding box logic
                 */
                if (Math.abs(gradientTransform.getScaleX() - gradientTransform.getScaleY())
                        > EPSILON)
                    isObjectBoundingBox = true;
            }
        }

        if (isObjectBoundingBox) {
            return linearGradientObjectBoundingBoxShading(paint, state);
        } else {
            return linearGradientUserSpaceOnUseShading(paint, state);
        }
    }

    private PDShading linearGradientObjectBoundingBoxShading(Paint paint, PaintApplierState state)
            throws IOException {
        /*
         * I found this Stack Overflow question to be useful: https://stackoverflow.com/questions/50617275/svg-linear-gradients-
         * objectboundingbox-vs-userspaceonuse SVG has 2 different gradient display modes objectBoundingBox & userSpaceOnUse The
         * default is objectBoundingBox. PDF Axial gradients seem to be capable of displaying in any manner, but the default is
         * the normal rendered at a 90 degree angle from the gradient vector. This looks like an SVG in userSpaceOnUse mode. So
         * the task becomes how can we map the default of one format to a non-default mode in another so that the PDF an axial
         * gradient looks like an SVG with a linear gradient.
         *
         * The approach I've used is as follows: Draw the axial gradient on a 1x1 box. A perfect square is a special case where
         * the PDF defaults display matches the SVG default display. Then, use the gradient transform attached to the paint to
         * warp the space containing the box & distort it to a larger rectangle (which may, or may not, still be a square). This
         * makes the gradient in the PDF look like the gradient in an SVG if the SVG is using the objectBoundingBox mode.
         *
         * Note: there is some trickery with shape inversion because SVGs lay out from the top down & PDFs lay out from the
         * bottom up.
         */
        PDShadingType3 shading = setupBasicLinearShading(paint, state);

        Point2D startPoint = clonePoint(getPropertyValue(paint, "getStartPoint"));
        Point2D endPoint = clonePoint(getPropertyValue(paint, "getEndPoint"));
        AffineTransform gradientTransform = getPropertyValue(paint, "getTransform");
        state.affineTransform.concatenate(gradientTransform);

        // noinspection unused
        MultipleGradientPaint.CycleMethod cycleMethod = getCycleMethod(paint);
        // noinspection unused
        MultipleGradientPaint.ColorSpaceType colorSpaceType = getColorSpaceType(paint);

        // Note: all of the start and end points I've seen for linear gradients
        // that use the objectBoundingBox mode define a 1x1 box. I don't know if
        // this can be guaranteed.
        setupShadingCoords(shading, startPoint, endPoint);

        // We need the rectangle here so that the call to clip(useEvenOdd)
        // in PdfBoxGraphics2D.java clips to the right frame of reference
        //
        // Note: tricky stuff follows . . .
        // We're deliberately creating a bounding box with a negative height.
        // Why? Because that contentsStream.transform() is going to invert it
        // so that it has a positive height. It will always invert because
        // SVGs & PDFs have opposite layout directions.
        // If we started with a positive height, then inverted to a negative height
        // we end up with a negative height clipping box in the output PDF
        // and some PDF viewers cannot handle that.
        // e.g. Adobe acrobat will display the PDF one way & Mac Preview
        // will display it another.
        float calculatedX = (float) Math.min(startPoint.getX(), endPoint.getX());
        float calculatedY = (float) Math.max(1.0f, Math.max(startPoint.getY(), endPoint.getY()));
        float calculatedWidth = Math.max(1.0f, Math.abs((float) (endPoint.getX() - startPoint.getX())));
        float negativeHeight = -1.0f * Math.max(1.0f, Math.abs((float) (endPoint.getY() - startPoint.getY())));
        state.contentStream.addRect(calculatedX, calculatedY, calculatedWidth, negativeHeight);
        state.paintApplierEnvironment.getGraphics2D().markPathIsOnStream();
        state.paintApplierEnvironment.getGraphics2D().internalClip(false);
        // Warp the 1x1 box containing the gradient to fill a larger rectangular space
        state.contentStream.transform(new Matrix(state.affineTransform));
        return shading;
    }

    private void setupShadingCoords(PDShadingType3 shading, Point2D startPoint, Point2D endPoint) {
        COSArray coords = new COSArray();
        coords.add(new COSFloat((float) startPoint.getX()));
        coords.add(new COSFloat((float) startPoint.getY()));
        coords.add(new COSFloat((float) endPoint.getX()));
        coords.add(new COSFloat((float) endPoint.getY()));
        shading.setCoords(coords);
    }

    /**
     * This is the default gradient mode for both SVG and java.awt gradients.
     */
    private PDShading linearGradientUserSpaceOnUseShading(Paint paint, PaintApplierState state)
            throws IOException {

        PDShadingType3 shading = setupBasicLinearShading(paint, state);

        Point2D startPoint = clonePoint(getPropertyValue(paint, "getStartPoint"));
        Point2D endPoint = clonePoint(getPropertyValue(paint, "getEndPoint"));
        AffineTransform gradientTransform = getPropertyValue(paint, "getTransform");
        state.affineTransform.concatenate(gradientTransform);

        // noinspection unused
        MultipleGradientPaint.CycleMethod cycleMethod = getCycleMethod(paint);
        // noinspection unused
        MultipleGradientPaint.ColorSpaceType colorSpaceType = getColorSpaceType(paint);

        state.affineTransform.transform(startPoint, startPoint);
        state.affineTransform.transform(endPoint, endPoint);

        setupShadingCoords(shading, startPoint, endPoint);

        return shading;
    }

    private PDShadingType3 setupBasicLinearShading(Paint paint, PaintApplierState state)
            throws IOException {
        PDShadingType3 shading = new PDShadingType3(new COSDictionary());
        Color[] colors = getPropertyValue(paint, "getColors");
        Color firstColor = colors[0];
        PDColor firstColorMapped = state.colorMapper.mapColor(state.contentStream, firstColor);
        applyAsStrokingColor(firstColor, state);
        float[] fractions = getPropertyValue(paint, "getFractions");
        PDFunctionType3 type3 = buildType3Function(colors, fractions, state);
        shading.setAntiAlias(true);
        shading.setShadingType(PDShading.SHADING_TYPE2);
        shading.setColorSpace(firstColorMapped.getColorSpace());
        shading.setFunction(type3);
        shading.setExtend(setupExtends());
        return shading;
    }

    private COSArray setupExtends() {
        COSArray extend = new COSArray();
        /*
         * We need to always extend the gradient
         */
        extend.add(COSBoolean.TRUE);
        extend.add(COSBoolean.TRUE);
        return extend;
    }

    /**
     * Map the cycleMethod of the GradientPaint to the java.awt.MultipleGradientPaint.CycleMethod enum.
     *
     * @param paint the paint to get the cycleMethod from (if not in any other way possible using reflection)
     * @return the CycleMethod
     */
    private MultipleGradientPaint.CycleMethod getCycleMethod(Paint paint) {
        if (paint instanceof MultipleGradientPaint)
            return ((MultipleGradientPaint) paint).getCycleMethod();
        if (paint.getClass().getPackage().getName().equals("org.apache.batik.ext.awt")) {
            setupBatikReflectionAccess(paint);
            Object cycleMethod = getPropertyValue(paint, "getCycleMethod");
            if (cycleMethod == BATIK_GRADIENT_NO_CYCLE)
                return MultipleGradientPaint.CycleMethod.NO_CYCLE;
            if (cycleMethod == BATIK_GRADIENT_REFLECT)
                return MultipleGradientPaint.CycleMethod.REFLECT;
            if (cycleMethod == BATIK_GRADIENT_REPEAT)
                return MultipleGradientPaint.CycleMethod.REPEAT;
        }
        return MultipleGradientPaint.CycleMethod.NO_CYCLE;
    }

    private MultipleGradientPaint.ColorSpaceType getColorSpaceType(Paint paint) {
        if (paint instanceof MultipleGradientPaint)
            return ((MultipleGradientPaint) paint).getColorSpace();
        if (paint.getClass().getPackage().getName().equals("org.apache.batik.ext.awt")) {
            setupBatikReflectionAccess(paint);
            Object cycleMethod = getPropertyValue(paint, "getColorSpace");
            if (cycleMethod == BATIK_COLORSPACE_SRGB)
                return MultipleGradientPaint.ColorSpaceType.SRGB;
            if (cycleMethod == BATIK_COLORSPACE_LINEAR_RGB)
                return MultipleGradientPaint.ColorSpaceType.LINEAR_RGB;
        }
        return MultipleGradientPaint.ColorSpaceType.SRGB;
    }

    private Object BATIK_GRADIENT_NO_CYCLE;
    private Object BATIK_GRADIENT_REFLECT;
    private Object BATIK_GRADIENT_REPEAT;
    private Object BATIK_COLORSPACE_SRGB;
    private Object BATIK_COLORSPACE_LINEAR_RGB;

    private void setupBatikReflectionAccess(Paint paint) {
        /*
         * As we don't have Batik on our class path we need to access it by reflection if the user application is using Batik
         */
        if (BATIK_GRADIENT_NO_CYCLE != null)
            return;

        try {
            Class<?> cls = paint.getClass();
            if (cls.getSimpleName().equals("MultipleGradientPaint")) {
                BATIK_GRADIENT_NO_CYCLE = cls.getDeclaredField("NO_CYCLE");
                BATIK_GRADIENT_REFLECT = cls.getDeclaredField("REFLECT");
                BATIK_GRADIENT_REPEAT = cls.getDeclaredField("REPEAT");
                BATIK_COLORSPACE_SRGB = cls.getDeclaredField("SRGB");
                BATIK_COLORSPACE_LINEAR_RGB = cls.getDeclaredField("LINEAR_RGB");
            }
        } catch (NoSuchFieldException ignored) {
            /*
             * Can not detect Batik CycleMethods :(
             */
        }
    }

    private PDShading buildRadialGradientShading(Paint paint, PaintApplierState state)
            throws IOException {
        /*
         * Batik has a copy of RadialGradientPaint, but it has the same structure as the AWT RadialGradientPaint. So we use
         * Reflection to access the fields of both these classes.
         */
        Color[] colors = getPropertyValue(paint, "getColors");
        Color firstColor = colors[0];
        PDColor firstColorMapped = state.colorMapper.mapColor(state.contentStream, firstColor);
        applyAsStrokingColor(firstColor, state);

        PDShadingType3 shading = new PDShadingType3(new COSDictionary());
        shading.setAntiAlias(true);
        shading.setShadingType(PDShading.SHADING_TYPE3);
        shading.setColorSpace(firstColorMapped.getColorSpace());
        float[] fractions = getPropertyValue(paint, "getFractions");
        Point2D centerPoint = clonePoint(getPropertyValue(paint, "getCenterPoint"));
        Point2D focusPoint = clonePoint(getPropertyValue(paint, "getFocusPoint"));
        AffineTransform gradientTransform = getPropertyValue(paint, "getTransform");
        state.affineTransform.concatenate(gradientTransform);
        state.affineTransform.transform(centerPoint, centerPoint);
        state.affineTransform.transform(focusPoint, focusPoint);

        float radius = getPropertyValue(paint, "getRadius");
        radius = (float) Math.abs(radius * state.affineTransform.getScaleX());

        COSArray coords = new COSArray();

        coords.add(new COSFloat((float) centerPoint.getX()));
        coords.add(new COSFloat((float) centerPoint.getY()));
        coords.add(new COSFloat(0));
        coords.add(new COSFloat((float) focusPoint.getX()));
        coords.add(new COSFloat((float) focusPoint.getY()));
        coords.add(new COSFloat(radius));
        shading.setCoords(coords);

        PDFunctionType3 type3 = buildType3Function(colors, fractions, state);

        shading.setFunction(type3);
        shading.setExtend(setupExtends());
        return shading;
    }

    private PDShading buildGradientShading(GradientPaint gradientPaint, PaintApplierState state)
            throws IOException {
        Color[] colors = new Color[]{gradientPaint.getColor1(), gradientPaint.getColor2()};
        Color firstColor = colors[0];
        PDColor firstColorMapped = state.colorMapper.mapColor(state.contentStream, firstColor);
        applyAsStrokingColor(firstColor, state);

        PDShadingType3 shading = new PDShadingType3(new COSDictionary());
        shading.setShadingType(PDShading.SHADING_TYPE2);
        shading.setColorSpace(firstColorMapped.getColorSpace());
        float[] fractions = new float[]{0, 1};
        PDFunctionType3 type3 = buildType3Function(colors, fractions, state);

        Point2D startPoint = gradientPaint.getPoint1();
        Point2D endPoint = gradientPaint.getPoint2();

        state.affineTransform.transform(startPoint, startPoint);
        state.affineTransform.transform(endPoint, endPoint);

        setupShadingCoords(shading, startPoint, endPoint);

        shading.setFunction(type3);
        shading.setExtend(setupExtends());
        return shading;
    }

    private void applyTexturePaint(TexturePaint texturePaint, PaintApplierState state)
            throws IOException {
        Rectangle2D anchorRect = texturePaint.getAnchorRect();
        PDTilingPattern pattern = new PDTilingPattern();
        pattern.setPaintType(PDTilingPattern.PAINT_COLORED);
        pattern.setTilingType(PDTilingPattern.TILING_CONSTANT_SPACING_FASTER_TILING);

        pattern.setBBox(new PDRectangle((float) anchorRect.getX(), (float) anchorRect.getY(),
                (float) anchorRect.getWidth(), (float) anchorRect.getHeight()));
        pattern.setXStep((float) anchorRect.getWidth());
        pattern.setYStep((float) anchorRect.getHeight());

        AffineTransform patternTransform = new AffineTransform();
        patternTransform.translate(0, anchorRect.getHeight());
        patternTransform.scale(1f, -1f);
        pattern.setMatrix(patternTransform);

        PDAppearanceStream appearance = new PDAppearanceStream(state.document);
        appearance.setResources(pattern.getResources());
        appearance.setBBox(pattern.getBBox());

        PDPageContentStream imageContentStream = new PDPageContentStream(state.document, appearance,
                ((COSStream) pattern.getCOSObject()).createOutputStream());
        BufferedImage texturePaintImage = texturePaint.getImage();
        PDImageXObject imageXObject = state.imageEncoder
                .encodeImage(state.document, imageContentStream, texturePaintImage);

        float ratioW = (float) ((anchorRect.getWidth()) / texturePaintImage.getWidth());
        float ratioH = (float) ((anchorRect.getHeight()) / texturePaintImage.getHeight());
        float paintHeight = (texturePaintImage.getHeight()) * ratioH;
        if (state.nestedTransform != null) {
            imageContentStream.transform(new Matrix(state.nestedTransform));
        }
        imageContentStream.drawImage(imageXObject, (float) anchorRect.getX(),
                (float) (paintHeight + anchorRect.getY()), texturePaintImage.getWidth() * ratioW,
                -paintHeight);
        imageContentStream.close();

        PDColorSpace patternCS1 = new PDPattern(null, imageXObject.getColorSpace());
        COSName tilingPatternName = state.resources.add(pattern);
        PDColor patternColor = new PDColor(tilingPatternName, patternCS1);

        state.contentStream.setNonStrokingColor(patternColor);
        state.contentStream.setStrokingColor(patternColor);
    }

    /**
     * Encode a color gradient as a type3 function
     *
     * @param colors    The colors to encode
     * @param fractions the fractions for encoding
     * @param state     our state, this is needed for color mapping
     * @return the type3 function
     */
    private PDFunctionType3 buildType3Function(Color[] colors, float[] fractions,
                                               PaintApplierState state) {
        COSDictionary function = new COSDictionary();
        function.setInt(COSName.FUNCTION_TYPE, 3);

        COSArray domain = new COSArray();
        domain.add(new COSFloat(0));
        domain.add(new COSFloat(1));
        COSArray encode = new COSArray();
        COSArray range = new COSArray();
        range.add(new COSFloat(0));
        range.add(new COSFloat(1));
        List<Color> colorList = new ArrayList<>(Arrays.asList(colors));
        COSArray bounds = new COSArray();
        if (Math.abs(fractions[0]) > EPSILON) {
            colorList.add(0, colors[0]);
            bounds.add(new COSFloat(fractions[0]));
        }
        for (int i = 1; i < fractions.length - 1; i++) {
            float fraction = fractions[i];
            bounds.add(new COSFloat(fraction));
        }
        if (Math.abs(fractions[fractions.length - 1] - 1f) > EPSILON) {
            colorList.add(colors[colors.length - 1]);
            bounds.add(new COSFloat(fractions[fractions.length - 1]));
        }
        COSArray type2Functions = buildType2Functions(colorList, domain, encode, state);
        function.setItem(COSName.FUNCTIONS, type2Functions);
        function.setItem(COSName.BOUNDS, bounds);
        function.setItem(COSName.ENCODE, encode);
        PDFunctionType3 type3 = new PDFunctionType3(function);
        type3.setDomainValues(domain);
        return type3;
    }

    /**
     * Build a type2 function to interpolate between the given colors.
     *
     * @param colors the color to encode
     * @param domain the domain which should already been setuped. It will be used for the Type2 function
     * @param encode will get the domain information per color channel, i.e. colors.length x [0, 1]
     * @param state  our internal state, this is needed for color mapping
     * @return the Type2 function COSArray
     */
    private COSArray buildType2Functions(List<Color> colors, COSArray domain, COSArray encode,
                                         PaintApplierState state) {
        Color prevColor = colors.get(0);
        COSArray functions = new COSArray();
        for (int i = 1; i < colors.size(); i++) {
            Color color = colors.get(i);
            PDColor prevPdColor = state.colorMapper.mapColor(state.contentStream, prevColor);
            PDColor pdColor = state.colorMapper.mapColor(state.contentStream, color);
            COSArray c0 = new COSArray();
            COSArray c1 = new COSArray();
            for (float component : prevPdColor.getComponents()) {
                c0.add(new COSFloat(component));
            }
            for (float component : pdColor.getComponents()) {
                c1.add(new COSFloat(component));
            }
            COSDictionary type2Function = new COSDictionary();
            type2Function.setInt(COSName.FUNCTION_TYPE, 2);
            type2Function.setItem(COSName.C0, c0);
            type2Function.setItem(COSName.C1, c1);
            type2Function.setInt(COSName.N, 1);
            type2Function.setItem(COSName.DOMAIN, domain);
            functions.add(type2Function);
            encode.add(new COSFloat(0));
            encode.add(new COSFloat(1));
            prevColor = color;
        }
        return functions;
    }

    /**
     * Get a property value from an object using reflection
     *
     * @param obj            The object to get a property from.
     * @param propertyGetter method name of the getter, i.e. "getXY".
     * @param <T>            the type of the property you want to get.
     * @return the value read from the object
     */
    @SuppressWarnings("unchecked")
    public static <T> T getPropertyValue(Object obj, String propertyGetter) {
        if (obj == null) {
            return null;
        }
        Class<?> c = obj.getClass();
        while (c != null) {
            try {
                Method m = c.getMethod(propertyGetter, (Class<?>[]) null);
                return (T) m.invoke(obj);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
            c = c.getSuperclass();
        }
        throw new NullPointerException("Method " + propertyGetter + " not found on object " + obj);
    }

    protected static class PaintApplierState {
        protected PDDocument document;
        protected PDPageContentStream contentStream;
        protected ColorMapper colorMapper;
        protected ImageEncoder imageEncoder;
        protected PDResources resources;
        protected PDExtendedGraphicsState pdExtendedGraphicsState;
        protected Composite composite;
        private COSDictionary dictExtendedState;
        private PaintApplierEnvironment paintApplierEnvironment;
        private AffineTransform affineTransform;
        protected AffineTransform nestedTransform;

        private void ensureExtendedState() {
            if (pdExtendedGraphicsState == null) {
                this.dictExtendedState = new COSDictionary();
                this.dictExtendedState.setItem(COSName.TYPE, COSName.EXT_G_STATE);
                pdExtendedGraphicsState = new PDExtendedGraphicsState(this.dictExtendedState);
            }
        }
    }

    private static abstract class COSResourceCacheBase<TObject extends COSObjectable> {
        private final Map<Integer, List<TObject>> states = new HashMap<>();

        private static boolean equalsCOSDictionary(COSDictionary cosDictionary,
                                                   COSDictionary cosDictionary1) {
            if (cosDictionary.size() != cosDictionary1.size()) {
                return false;
            }
            for (COSName name : cosDictionary.keySet()) {
                COSBase item = cosDictionary.getItem(name);
                COSBase item2 = cosDictionary1.getItem(name);
                if (!equalsCOSBase(item, item2))
                    return false;
            }
            return true;
        }

        private static boolean equalsCOSBase(COSBase item, COSBase item2) {
            if (item == item2) {
                return true;
            }
            if (item == null) {
                return false;
            }
            if (item2 == null) {
                return false;
            }
            if (item.equals(item2)) {
                return true;
            }
            if (item instanceof COSDictionary && item2 instanceof COSDictionary) {
                return equalsCOSDictionary((COSDictionary) item, (COSDictionary) item2);
            }
            if (item instanceof COSArray && item2 instanceof COSArray) {
                return equalsCOSArray((COSArray) item, (COSArray) item2);
            }
            return false;
        }

        private static boolean equalsCOSArray(COSArray item, COSArray item2) {
            if (item.size() != item2.size()) {
                return false;
            }
            for (int i = 0; i < item.size(); i++) {
                COSBase i1 = item.getObject(i);
                COSBase i2 = item2.getObject(i);
                if (!equalsCOSBase(i1, i2)) {
                    return false;
                }
            }
            return true;
        }

        protected abstract int getKey(TObject obj);

        TObject makeUnique(TObject state) {
            int key = getKey(state);
            List<TObject> pdExtendedGraphicsStates = states.computeIfAbsent(key, k -> new ArrayList<>());
            for (TObject s : pdExtendedGraphicsStates) {
                if (stateEquals(s, state)) {
                    return s;
                }
            }
            pdExtendedGraphicsStates.add(state);
            return state;
        }

        private boolean stateEquals(TObject s, TObject state) {
            COSBase base1 = s.getCOSObject();
            COSBase base2 = state.getCOSObject();
            return equalsCOSBase(base1, base2);
        }
    }

    private static class ExtGStateCache extends COSResourceCacheBase<PDExtendedGraphicsState> {
        @Override
        protected int getKey(PDExtendedGraphicsState obj) {
            return obj.getCOSObject().size();
        }
    }

    private static class PDShadingCache extends COSResourceCacheBase<PDShading> {
        @Override
        protected int getKey(PDShading obj) {
            return obj.getCOSObject().size();
        }
    }
}
