import 'dart:io';

import 'package:jni/jni.dart';
import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart'
    as mupdf;

export 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart';

class MuPDF {
  mupdf.Document open(File file) {
    return mupdf.Document.openDocument(file.path.toJString());
  }

  String getMetadata(mupdf.Document document, String key) {
    return document
        .getMetaData(key.toJString())
        .toDartString(deleteOriginal: true);
  }
}
