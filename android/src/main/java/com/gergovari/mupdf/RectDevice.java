package com.gergovari.mupdf;

import android.util.Log;

import com.artifex.mupdf.fitz.ColorSpace;
import com.artifex.mupdf.fitz.DefaultColorSpaces;
import com.artifex.mupdf.fitz.Device;
import com.artifex.mupdf.fitz.DocumentWriter;
import com.artifex.mupdf.fitz.Image;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Path;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.Shade;
import com.artifex.mupdf.fitz.StrokeState;
import com.artifex.mupdf.fitz.Text;

@SuppressWarnings("unused")
public class RectDevice extends Device {
    public RectDevice(String path) {
        writer = new DocumentWriter(path, "PDF", "pretty,ascii,compress-images,compress-fonts");
    }

    DocumentWriter writer;
    public Device current;
    private float highest = Float.POSITIVE_INFINITY;
    public float lowest = 0;
    private float getLowest(Rect rect) {
        if (rect.y1 <= 842) {
            rect.offset(0, -highest);
            return Float.max(rect.y1, lowest);
        }
        return lowest;
    }

    Rect filter;
    public Device filterDevice(Rect target, float newHighest) {
        filter = target;
        highest = newHighest;
        lowest = 0;
        return this;
    }
    private Boolean isInFilter(Rect rect) {
        Boolean result = !(rect.x1 <= filter.x0 || rect.x0 >= filter.x1 || rect.y1 <= filter.y0 || rect.y0 >= filter.y1);
        if (result) {
            lowest = getLowest(rect);
        }
        return result && (rect.x0 >= 0 && rect.x1 >= 0 && rect.y0 <= 842 && rect.y1 <= 842);
    }

    public void beginPage() {
        current = writer.beginPage(new Rect(0, 0, 595, 842));
    }

    public void endPage() {
        writer.endPage();
    }

    public void done() {
        writer.close();
    }

    @Override
    public void close() {
    }

    @Override
    public void fillPath(Path path, boolean b, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.fillPath(path, b, matrix.concat(Matrix.Translate(0, -highest)), colorSpace, floats, v, i);
        }
    }

    @Override
    public void strokePath(Path path, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(path.getBounds(strokeState, matrix))) {
            current.strokePath(path, strokeState, matrix.concat(Matrix.Translate(0, -highest)), colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipPath(Path path, boolean b, Matrix matrix) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.clipPath(path, b, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void clipStrokePath(Path path, StrokeState strokeState, Matrix matrix) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.clipStrokePath(path, strokeState, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void fillText(Text text, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix))) {
            current.fillText(text, matrix.concat(Matrix.Translate(0, -highest)), colorSpace, floats, v, i);
        }
    }

    @Override
    public void strokeText(Text text, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(text.getBounds(strokeState, matrix))) {
            current.strokeText(text, strokeState, matrix.concat(Matrix.Translate(0, -highest)), colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipText(Text text, Matrix matrix) {
        if (isInFilter(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix))) {
            current.clipText(text, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void clipStrokeText(Text text, StrokeState strokeState, Matrix matrix) {
        if (isInFilter(text.getBounds(strokeState, matrix))) {
            current.clipStrokeText(text, strokeState, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void ignoreText(Text text, Matrix matrix) {
        if (isInFilter(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix))) {
            current.ignoreText(text, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void fillShade(Shade shade, Matrix matrix, float v, int i) {
        // FIXME: not filtered
        current.fillShade(shade, matrix.concat(Matrix.Translate(0, -highest)), v, i);
    }

    @Override
    public void fillImage(Image image, Matrix matrix, float v, int i) {
        // FIXME: check if works
        if (isInFilter(image.toPixmap().getBounds())) {
            current.fillImage(image, matrix.concat(Matrix.Translate(0, -highest)), v, i);
        }
    }

    @Override
    public void fillImageMask(Image image, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(image.toPixmap().getBounds())) {
            current.fillImageMask(image, matrix.concat(Matrix.Translate(0, -highest)), colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipImageMask(Image image, Matrix matrix) {
        if (isInFilter(image.toPixmap().getBounds())) {
            current.clipImageMask(image, matrix.concat(Matrix.Translate(0, -highest)));
        }
    }

    @Override
    public void popClip() {
        current.popClip();
    }

    private Boolean needMaskBalance = false;
    @Override
    public void beginMask(Rect rect, boolean b, ColorSpace colorSpace, float[] floats, int i) {
        if (isInFilter(rect)) {
            needMaskBalance = true;
            rect.offset(0, -highest);
            current.beginMask(rect, b, colorSpace, floats, i);
        }
    }

    @Override
    public void endMask() {
        if (needMaskBalance) {
            current.endMask();
            needMaskBalance = false;
        }
    }

    @Override
    public void beginGroup(Rect rect, ColorSpace colorSpace, boolean b, boolean b1, int i, float v) {
        Log.d("flutter", "beginGroup: ");
        Log.d("flutter", rect.toString());
        //current.beginGroup(rect, colorSpace, b, b1, i, v);
    }

    @Override
    public void endGroup() {
        //current.endGroup();
    }

    private Boolean needTileBalance = false;
    @Override
    public int beginTile(Rect rect, Rect rect1, float v, float v1, Matrix matrix, int i) {
        if (isInFilter(rect)) {
            needTileBalance = true;
            rect.offset(0, -highest);
            rect1.offset(0, -highest);
            return current.beginTile(rect, rect1, v, v1, matrix, i);
        }
        return 0;
    }

    @Override
    public void endTile() {
        if (needTileBalance) {
            needTileBalance = false;
            current.endTile();
        }
    }

    @Override
    public void renderFlags(int i, int i1) {
        current.renderFlags(i, i1);
    }

    @Override
    public void setDefaultColorSpaces(DefaultColorSpaces defaultColorSpaces) {
        current.setDefaultColorSpaces(defaultColorSpaces);
    }

    @Override
    public void beginLayer(String s) {
        current.beginLayer(s);
    }

    @Override
    public void endLayer() {
        current.endLayer();
    }

    @Override
    public void beginStructure(int i, String s, int i1) {
        current.beginStructure(i, s, i1);
    }

    @Override
    public void endStructure() {
        current.endStructure();
    }

    @Override
    public void beginMetatext(int i, String s) {
        //current.beginMetatext(i, s);
    }

    @Override
    public void endMetatext() {
        //current.endMetatext();
    }
}
