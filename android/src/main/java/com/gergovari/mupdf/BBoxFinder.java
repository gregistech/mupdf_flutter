package com.gergovari.mupdf;

import com.artifex.mupdf.fitz.*;

class Bounder {
    Rect bbox = new Rect(
            Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
            Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);

    void extend(float x, float y) {
        if (x < bbox.x0) {
            bbox.x0 = x;
        }
        if (x > bbox.x1) {
            bbox.x1 = x;
        }
        if (y < bbox.y0) {
            bbox.y0 = y;
        }
        if (y > bbox.y1) {
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

    @Override
    public void moveTo(float x, float y) {
        _bounder.extendPoint(_matrix, x, y);
    }

    @Override
    public void lineTo(float x, float y) {
        _bounder.extendPoint(_matrix, x, y);
    }

    @Override
    public void curveTo(float x1, float y1, float x2, float y2, float x3, float y3) {
        _bounder.extendPoint(_matrix, x1, y1);
        _bounder.extendPoint(_matrix, x2, y2);
        _bounder.extendPoint(_matrix, x3, y3);
    }

    @Override
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

    @Override
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
    //private final Device device = new DrawDevice(new Pixmap(ColorSpace.DeviceRGB, new Rect(0,0,0,0)));
    private final Device device = new TraceDevice();
    //private final Device device = new BBoxDevice();

    public Rect getBounds(Page page) {
        page.run(device, Matrix.Identity(), new Cookie());
        //return device.getBounds();
        return new Rect(0,0,0,0);
    }
}


class TraceDevice extends Device implements PathWalker, TextWalker
{
	public String traceColor(ColorSpace cs, float[] color, float alpha) {
		String s = cs + " [";
		int i;
		for (i = 0; i < color.length; ++i) {
			if (i > 0) s += " ";
			s += color[i];
		}
		return s + "] " + alpha;
	}
	public String traceStroke(StrokeState stroke) {
		return "c=" + stroke.getStartCap() + "," + stroke.getDashCap() + "," + stroke.getEndCap() +
			" j=" + stroke.getLineJoin() +
			" m=" + stroke.getMiterLimit() +
			" l=" + stroke.getLineWidth();
	}

	public void moveTo(float x, float y) {
		System.out.println("moveto " + x + " " + y);
	}

	public void lineTo(float x, float y) {
		System.out.println("lineto " + x + " " + y);
	}

	public void curveTo(float cx1, float cy1, float cx2, float cy2, float ex, float ey) {
		System.out.println("curveto " + cx1 + " " + cy1 + " " + cx2 + " " + cy2 + " " + ex + " " + ey);
	}

	public void closePath() {
		System.out.println("closepath");
	}

	public void showGlyph(Font font, Matrix trm, int glyph, int unicode, boolean wmode) {
		System.out.println("glyph '" + (char)unicode + "' " + glyph + "\t" + font + " " + trm);
	}

	public void tracePath(Path path) {
		path.walk(this);
	}

	public void traceText(Text text) {
		text.walk(this);
	}

	public void close() {
	}

	public void fillPath(Path path, boolean evenOdd, Matrix ctm, ColorSpace cs, float[] color, float alpha, int cp) {
		System.out.println("fillPath " + evenOdd + " " + ctm + " " + traceColor(cs, color, alpha));
		tracePath(path);
	}

	public void strokePath(Path path, StrokeState stroke, Matrix ctm, ColorSpace cs, float[] color, float alpha, int cp) {
		System.out.println("strokePath " + traceStroke(stroke) + " " + ctm + " " + traceColor(cs, color, alpha));
		tracePath(path);
	}

	public void clipPath(Path path, boolean evenOdd, Matrix ctm) {
		System.out.println("clipPath " + evenOdd + " " + ctm);
		tracePath(path);
	}

	public void clipStrokePath(Path path, StrokeState stroke, Matrix ctm) {
		System.out.println("clipStrokePath " + traceStroke(stroke) + " " + ctm);
		tracePath(path);
	}

	public void fillText(Text text, Matrix ctm, ColorSpace cs, float[] color, float alpha, int cp) {
		System.out.println("fillText " + ctm + " " + traceColor(cs, color, alpha));
		traceText(text);
	}

	public void strokeText(Text text, StrokeState stroke, Matrix ctm, ColorSpace cs, float[] color, float alpha, int cp) {
		System.out.println("strokeText " + ctm + " " + traceStroke(stroke) + " " + traceColor(cs, color, alpha));
		traceText(text);
	}

	public void clipText(Text text, Matrix ctm) {
		System.out.println("clipText " + ctm);
		traceText(text);
	}

	public void clipStrokeText(Text text, StrokeState stroke, Matrix ctm) {
		System.out.println("clipStrokeText " + ctm + " " + traceStroke(stroke));
		traceText(text);
	}

	public void ignoreText(Text text, Matrix ctm) {
		System.out.println("ignoreText " + ctm);
		traceText(text);
	}

	public void fillShade(Shade shd, Matrix ctm, float alpha, int cp) {
		System.out.println("fillShade " + ctm + " " + alpha);
	}

	public void fillImage(Image img, Matrix ctm, float alpha, int cp) {
		System.out.println("fillImage " + ctm + " " + alpha);
	}

	public void fillImageMask(Image img, Matrix ctm, ColorSpace cs, float[] color, float alpha, int cp) {
		System.out.println("fillImageMask " + ctm + " " + traceColor(cs, color, alpha));
	}

	public void clipImageMask(Image img, Matrix ctm) {
		System.out.println("clipImageMask " + ctm);
	}

	public void popClip() {
		System.out.println("popClip");
	}

	public void beginMask(Rect rect, boolean luminosity, ColorSpace cs, float[] bc, int cp) {
		System.out.println("beginMask r=" + rect +
				" l=" + luminosity +
				" " + traceColor(cs, bc, 1));
	}

	public void endMask() {
		System.out.println("endMask");
	}

	public void beginGroup(Rect rect, ColorSpace cs, boolean isolated, boolean knockout, int blendmode, float alpha) {
		System.out.println("beginGroup r=" + rect +
				" i=" + isolated +
				" k=" + knockout +
				" bm=" + blendmode +
				" a=" + alpha);
	}

	public void endGroup() {
		System.out.println("endGroup");
	}

	public int beginTile(Rect area, Rect view, float xstep, float ystep, Matrix ctm, int id) {
		System.out.println("beginTile");
		return 0;
	}

	public void endTile() {
		System.out.println("endTile");
	}

	public void renderFlags(int set, int clear) {
		System.out.println("renderFlags set=" + set + " clear=" + clear);
	}

	public void setDefaultColorSpaces(DefaultColorSpaces dcs) {
		System.out.println("setDefaultColorSpaces" +
			" gray=" + dcs.getDefaultGray() +
			" rgb=" + dcs.getDefaultRGB() +
			" cmyk=" + dcs.getDefaultCMYK() +
			" outputIntent=" + dcs.getOutputIntent());
	}

	public void beginLayer(String name) {
		System.out.println("beginLayer");
	}

	public void endLayer() {
		System.out.println("endLayer");
	}

	public void beginStructure(int standard, String raw, int uid) {
		System.out.println("beginStructure standard=" + standard +
			" raw=" + raw +
			" uid=" + uid);
	}

	public void endStructure() {
		System.out.println("endStructure");
	}

	public void beginMetatext(int meta, String text) {
		System.out.println("beginMetatext type=" + meta +
			" text=" + text);
	}

	public void endMetatext() {
		System.out.println("endMetatext");
	}
}