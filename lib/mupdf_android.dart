import 'dart:io';

import 'package:jni/jni.dart';
import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart'
    as mupdf;

export 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart';
export 'package:mupdf_android/src/third_party/com/gergovari/mupdf/_package.dart';
export 'package:mupdf_android/extensions.dart';

class MuPDF {
  static Map<String, mupdf.Rect> MEDIABOXES = {
    "A4": mupdf.Rect.ctor1(0, 0, 595, 842)
  };

  mupdf.Document open(File file) {
    return mupdf.Document.openDocument(file.path.toJString());
  }
}
