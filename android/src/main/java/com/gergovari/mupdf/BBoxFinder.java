package com.gergovari.mupdf;

import com.artifex.mupdf.fitz.*;

class Bounder {
    final Rect bbox = new Rect(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);

    void extend(float x, float y) {
        if (x < bbox.x0) {
            System.out.println("x0: " + x);
            bbox.x0 = x;
        }
        if (y < bbox.y0) {
            System.out.println("y0: " + y);
            bbox.y0 = y;
        }
        if (x > bbox.x1) {
            System.out.println("x1: " + x);
            bbox.x1 = x;
        }
        if (y > bbox.y1) {
            System.out.println("y1: " + y);
            bbox.y1 = y;
        }
    }

    void extendPoint(Matrix m, float px, float py) {
        float x = px * m.a + py * m.c + m.e;
        float y = px * m.b + py * m.d + m.f;
        extend(x, y);
    }

    void extendRect(Matrix m, Rect r) {
        extendPoint(m, r.x0, r.y0);
        extendPoint(m, r.x1, r.y0);
        extendPoint(m, r.x0, r.y1);
        extendPoint(m, r.x1, r.y1);
    }
}

class PathBounder implements PathWalker {
    private final Bounder _bounder;
    private final Matrix _matrix;

    public PathBounder(Bounder _bounder, Matrix _matrix) {
        this._bounder = _bounder;
        this._matrix = _matrix;
    }

    public void moveTo(float x, float y) {
        _bounder.extendPoint(_matrix, x, y);
    }

    public void lineTo(float x, float y) {
        _bounder.extendPoint(_matrix, x, y);
    }

    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        _bounder.extendPoint(_matrix, x1, y1);
        _bounder.extendPoint(_matrix, x2, y2);
        _bounder.extendPoint(_matrix, x3, y3);
    }

    public void closePath() {
    }
}

class TextBounder implements TextWalker {
    private final Bounder _bounder;
    private final Matrix _matrix;

    public TextBounder(Bounder _bounder, Matrix _matrix) {
        this._bounder = _bounder;
        this._matrix = _matrix;
    }

    public void showGlyph(Font font, Matrix trm, int gid, int ucs, boolean bidi) {
        Rect bbox = new Rect((float) 0, (float)-0.2, font.advanceGlyph(gid, false), (float) 0.8);
        _bounder.extendRect(trm.concat(_matrix), bbox);
    }
}

class BBoxDevice extends Device {
    private final Bounder _bounder = new Bounder();

    public void close() {}

    public void fillPath(Path path, boolean z, Matrix matrix, ColorSpace colorSpace, float[] fs, float f, int i) {
        path.walk(new PathBounder(_bounder, matrix));
    }

    public void clipPath(Path path, boolean z, Matrix matrix) {
        path.walk(new PathBounder(_bounder, matrix));
    }

    public void strokePath(Path path, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] fs, float f, int i) {
        path.walk(new PathBounder(_bounder, matrix));
    }

    public void clipStrokePath(Path path, StrokeState strokeState, Matrix matrix) {
        path.walk(new PathBounder(_bounder, matrix));
    }

    public void fillText(Text text, Matrix matrix, ColorSpace colorSpace, float[] fs, float f, int i) {
        text.walk(new TextBounder(_bounder, matrix));
    }

    public void clipText(Text text, Matrix matrix) {
        text.walk(new TextBounder(_bounder, matrix));
    }

    public void strokeText(Text text, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] fs, float f, int i) {
        text.walk(new TextBounder(_bounder, matrix));
    }

    public void clipStrokeText(Text text, StrokeState strokeState, Matrix matrix) {
        text.walk(new TextBounder(_bounder, matrix));
    }

    public void ignoreText(Text text, Matrix matrix) {
        text.walk(new TextBounder(_bounder, matrix));
    }

    public void fillShade(Shade shade, Matrix matrix, float f, int i) {
        Rect bbox = new Rect(0, 0, 0, 0);
        _bounder.extend(bbox.x0, bbox.y0);
        _bounder.extend(bbox.x1, bbox.y1);
    }

    public void fillImage(Image image, Matrix matrix, float f, int i) {
        _bounder.extendRect(matrix, new Rect(0, 0, 1, 1));
    }

    public void fillImageMask(Image image, Matrix matrix, ColorSpace colorSpace, float[] fs, float f, int i) {
        _bounder.extendRect(matrix, new Rect(0, 0, 1, 1));
    }

    public void clipImageMask(Image image, Matrix matrix) {

    }

    public void popClip() {

    }

    public void beginMask(Rect rect, boolean b, ColorSpace colorSpace, float[] floats, int i) {

    }

    public void endMask() {

    }

    public void beginGroup(Rect rect, ColorSpace colorSpace, boolean b, boolean b1, int i, float v) {

    }

    public void endGroup() {

    }

    public int beginTile(Rect rect, Rect rect1, float v, float v1, Matrix matrix, int i) {
        return 0;
    }

    public void endTile() {

    }

    public void renderFlags(int i, int i1) {

    }

    public void setDefaultColorSpaces(DefaultColorSpaces defaultColorSpaces) {

    }

    public void beginLayer(String s) {

    }

    public void endLayer() {

    }

    public void beginStructure(int i, String s, int i1) {

    }

    public void endStructure() {

    }

    public void beginMetatext(int i, String s) {

    }

    public void endMetatext() {

    }

    public Rect getBounds() {
        return _bounder.bbox;
    }
}

@SuppressWarnings("unused")
public class BBoxFinder {
    public Rect getBounds(Page page) {
		final BBoxDevice device = new BBoxDevice();
        page.run(device, Matrix.Identity(), new Cookie());
        return device.getBounds();
    }
}

