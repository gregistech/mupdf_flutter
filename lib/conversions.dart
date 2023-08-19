import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart'
    as mupdf;

extension DocumentConversion on mupdf.Document {
  mupdf.PDFDocument? toPDFDocument() {
    if (isPDF()) {
      return castTo(mupdf.PDFDocument.type);
    } else {
      return null;
    }
  }
}

extension PageConversion on mupdf.Page {
  mupdf.PDFPage toPDFPage() {
    return castTo(mupdf.PDFPage.type);
  }

  mupdf.PDFObject toPDFObject() {
    return castTo(mupdf.PDFObject.type);
  }
}
