import 'package:jni/jni.dart';
import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart';

import 'document_page_list.dart';

extension Pages on Document {
  int get pageCount {
    return countPages(0);
  }

  DocumentPageList get pages {
    return DocumentPageList(this);
  }
}

extension ReplacePage on PDFDocument {
  void replacePage(int i, PDFPage page) {
    deletePage(i);
    insertPage(i, page.castTo(PDFObject.type));
  }
}

extension FindPage on Document {
  int findPageIndex(Page page) {
    return this.pages.indexOf(page);
  }
}

extension Metadata on Document {
  String getMetadata(String key) {
    return getMetaData(key.toJString()).toDartString(deleteOriginal: true);
  }
}
