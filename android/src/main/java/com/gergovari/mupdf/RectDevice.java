package com.gergovari.mupdf;

import android.util.Log;

import com.artifex.mupdf.fitz.ColorSpace;
import com.artifex.mupdf.fitz.DefaultColorSpaces;
import com.artifex.mupdf.fitz.Device;
import com.artifex.mupdf.fitz.DocumentWriter;
import com.artifex.mupdf.fitz.FileStream;
import com.artifex.mupdf.fitz.Image;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Path;
import com.artifex.mupdf.fitz.Point;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.Shade;
import com.artifex.mupdf.fitz.StrokeState;
import com.artifex.mupdf.fitz.Text;

import java.io.IOException;

@SuppressWarnings("unused")
public class RectDevice extends Device {
    public RectDevice(String path) throws IOException {
        writer = new DocumentWriter(path, "PDF", "pretty,ascii,compress-images,compress-fonts");
    }

    DocumentWriter writer;
    public Device current;
    Rect filter;
    float lowest = 0;

    public Device filterDevice(Rect target) {
        filter = target;
        return this;
    }

    public void beginPage() {
        current = writer.beginPage(new Rect(0, 0, 595, 842));
    }

    public void endPage() {
        writer.endPage();
    }

    public void done() throws IOException {
        writer.close();
    }

    @Override
    public void close() {
    }

    private Boolean isInFilter(Rect rect) {
        return !(rect.x1 <= filter.x0 || rect.x0 >= filter.x1 || rect.y1 <= filter.y0 || rect.y0 >= filter.y1);
    }

    @Override
    public void fillPath(Path path, boolean b, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.fillPath(path, b, matrix, colorSpace, floats, v, i);
        }
    }

    @Override
    public void strokePath(Path path, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(path.getBounds(strokeState, matrix))) {
            current.strokePath(path, strokeState, matrix, colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipPath(Path path, boolean b, Matrix matrix) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.clipPath(path, b, matrix);
        }
    }

    @Override
    public void clipStrokePath(Path path, StrokeState strokeState, Matrix matrix) {
        if (isInFilter(path.getBounds(new StrokeState(0,0,0,0,0), matrix))) {
            current.clipStrokePath(path, strokeState, matrix);
        }
    }

    @Override
    public void fillText(Text text, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        Rect bounds = text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix);
        if (isInFilter(bounds)) {
            current.fillText(text, matrix.translate(0, -bounds.y0), colorSpace, floats, v, i);
        }
    }

    @Override
    public void strokeText(Text text, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(text.getBounds(strokeState, matrix))) {
            current.strokeText(text, strokeState, matrix, colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipText(Text text, Matrix matrix) {
        if (isInFilter(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix))) {
            current.clipText(text, matrix);
        }
    }

    @Override
    public void clipStrokeText(Text text, StrokeState strokeState, Matrix matrix) {
        if (isInFilter(text.getBounds(strokeState, matrix))) {
            current.clipStrokeText(text, strokeState, matrix);
        }
    }

    @Override
    public void ignoreText(Text text, Matrix matrix) {
        if (isInFilter(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix))) {
            current.ignoreText(text, matrix);
        }
    }

    @Override
    public void fillShade(Shade shade, Matrix matrix, float v, int i) {
        // FIXME: not filtered
        current.fillShade(shade, matrix, v, i);
    }

    @Override
    public void fillImage(Image image, Matrix matrix, float v, int i) {
        // FIXME: check if works
        if (isInFilter(image.toPixmap().getBounds())) {
            current.fillImage(image, matrix, v, i);
        }
    }

    @Override
    public void fillImageMask(Image image, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        if (isInFilter(image.toPixmap().getBounds())) {
            current.fillImageMask(image, matrix, colorSpace, floats, v, i);
        }
    }

    @Override
    public void clipImageMask(Image image, Matrix matrix) {
        if (isInFilter(image.toPixmap().getBounds())) {
            current.clipImageMask(image, matrix);
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
