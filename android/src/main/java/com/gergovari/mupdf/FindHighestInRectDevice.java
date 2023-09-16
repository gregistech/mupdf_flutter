package com.gergovari.mupdf;

import android.util.Log;

import com.artifex.mupdf.fitz.ColorSpace;
import com.artifex.mupdf.fitz.DefaultColorSpaces;
import com.artifex.mupdf.fitz.Device;
import com.artifex.mupdf.fitz.Image;
import com.artifex.mupdf.fitz.Matrix;
import com.artifex.mupdf.fitz.Path;
import com.artifex.mupdf.fitz.Rect;
import com.artifex.mupdf.fitz.Shade;
import com.artifex.mupdf.fitz.StrokeState;
import com.artifex.mupdf.fitz.Text;

@SuppressWarnings("unused")
public class FindHighestInRectDevice extends Device {

    public float highest = Float.POSITIVE_INFINITY;
    Rect filter;
    public Device filterDevice(Rect target) {
        filter = target;
        return this;
    }
    private Boolean isInFilter(Rect rect) {
        return !(rect.x1 <= filter.x0 || rect.x0 >= filter.x1 || rect.y1 <= filter.y0 || rect.y0 >= filter.y1);
    }

    private float getHighest(Rect rect) {
        if (isInFilter(rect) && rect.y0 >= 0) {
            return Float.min(rect.y0, highest);
        }
        return highest;
    }

    @Override
    public void close() {

    }

    @Override
    public void fillPath(Path path, boolean b, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        highest = getHighest(path.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix));
    }

    @Override
    public void strokePath(Path path, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {

        highest = getHighest(path.getBounds(strokeState, matrix));
    }

    @Override
    public void clipPath(Path path, boolean b, Matrix matrix) {
        highest = getHighest(path.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix));
    }

    @Override
    public void clipStrokePath(Path path, StrokeState strokeState, Matrix matrix) {
        highest = getHighest(path.getBounds(strokeState, matrix));
    }

    @Override
    public void fillText(Text text, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        highest = getHighest(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix));
    }

    @Override
    public void strokeText(Text text, StrokeState strokeState, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
        highest = getHighest(text.getBounds(strokeState, matrix));
    }

    @Override
    public void clipText(Text text, Matrix matrix) {
        highest = getHighest(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix));
    }

    @Override
    public void clipStrokeText(Text text, StrokeState strokeState, Matrix matrix) {
        highest = getHighest(text.getBounds(strokeState, matrix));
    }

    @Override
    public void ignoreText(Text text, Matrix matrix) {
        highest = getHighest(text.getBounds(new StrokeState(0, 0, 0, 0, 0), matrix));
    }

    @Override
    public void fillShade(Shade shade, Matrix matrix, float v, int i) {
    }

    @Override
    public void fillImage(Image image, Matrix matrix, float v, int i) {
    }

    @Override
    public void fillImageMask(Image image, Matrix matrix, ColorSpace colorSpace, float[] floats, float v, int i) {
    }

    @Override
    public void clipImageMask(Image image, Matrix matrix) {
    }

    @Override
    public void popClip() {
    }

    @Override
    public void beginMask(Rect rect, boolean b, ColorSpace colorSpace, float[] floats, int i) {
        highest = getHighest(rect);
    }

    @Override
    public void endMask() {

    }

    @Override
    public void beginGroup(Rect rect, ColorSpace colorSpace, boolean b, boolean b1, int i, float v) {
        highest = getHighest(rect);
    }

    @Override
    public void endGroup() {

    }

    @Override
    public int beginTile(Rect rect, Rect rect1, float v, float v1, Matrix matrix, int i) {
        highest = getHighest(rect);
        highest = getHighest(rect1);
        return 0;
    }

    @Override
    public void endTile() {

    }

    @Override
    public void renderFlags(int i, int i1) {

    }

    @Override
    public void setDefaultColorSpaces(DefaultColorSpaces defaultColorSpaces) {

    }

    @Override
    public void beginLayer(String s) {

    }

    @Override
    public void endLayer() {

    }

    @Override
    public void beginStructure(int i, String s, int i1) {

    }

    @Override
    public void endStructure() {

    }

    @Override
    public void beginMetatext(int i, String s) {

    }

    @Override
    public void endMetatext() {

    }
}
