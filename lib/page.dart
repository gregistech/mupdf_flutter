import 'package:jni/jni.dart';
import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart';

extension JArrayConversion<T extends JObject> on JArray<T> {
  List<T> toList() {
    List<T> elems = [];
    for (int j = 0; j < length; j++) {
      elems.add(this[j]);
    }
    return elems;
  }
}

extension Content on Page {
  String get text {
    return lines.fold(
        "",
        (previousValue, StructuredText_TextLine element) =>
            previousValue + element.text + "\n");
  }

  StructuredText get structuredText {
    return this.toStructuredText(
        "preserve-ligatures,preserve-whitespace,preserve-spans,preserve-images"
            .toJString());
  }

  List<StructuredText_TextBlock> get blocks {
    return structuredText.getBlocks().toList();
  }

  List<StructuredText_TextLine> get lines {
    List<StructuredText_TextLine> lines = [];
    for (StructuredText_TextBlock block in blocks) {
      lines.addAll(block.lines.toList());
    }
    return lines;
  }

  Pixmap rectToPixmap(Rect rect) {
    Pixmap pixmap = Pixmap.new4(ColorSpace.DeviceRGB, rect, true);
    DrawDevice device = DrawDevice.new2(pixmap);
    run(device, Matrix.Identity(), Cookie());
    return pixmap;
  }
}

extension Bounds on Page {
  Rect getMarkBounds() {
    /// TODO: implement BBoxDevice bounds
    return getBounds(0);
  }
}

extension TextExt on StructuredText_TextLine {
  String get text {
    return chars.toList().fold(
        "",
        (String previousValue, StructuredText_TextChar element) =>
            previousValue + String.fromCharCode(element.c));
  }
}
