import 'dart:math';
import 'package:jni/jni.dart';

import 'conversions.dart';
import 'document.dart';
import 'package:mupdf_android/src/third_party/com/artifex/mupdf/fitz/_package.dart'
    as mupdf;

class DocumentPagesIterator implements Iterator<mupdf.Page> {
  DocumentPageList _pages;
  DocumentPagesIterator(this._pages);

  int _i = 0;
  bool _reverse = false;

  @override
  mupdf.Page get current {
    return _pages[_i];
  }

  @override
  bool moveNext() {
    if (_i < _pages.length && _i >= 0) {
      _i += _reverse ? -1 : 1;
    }
    return _i < _pages.length && _i >= 0;
  }

  void reverse() {
    _reverse = true;
    _i = _pages.length - 1;
  }
}

class DocumentPageList implements List<mupdf.Page> {
  mupdf.Document _document;
  DocumentPageList(this._document);

  @override
  mupdf.Page get first {
    return _document.loadPage(0, 0);
  }

  @override
  set first(mupdf.Page value) {
    _document.toPDFDocument()?.replacePage(0, value.toPDFPage());
  }

  @override
  mupdf.Page get last {
    return _document.loadPage(-1, length - 1);
  }

  @override
  set last(mupdf.Page value) {
    _document.toPDFDocument()?.replacePage(-1, value.toPDFPage());
  }

  @override
  int get length {
    return _document.countPages(0);
  }

  @override
  set length(int newLength) {
    // TODO: implement length
  }

  @override
  List<mupdf.Page> operator +(List<mupdf.Page> other) {
    addAll(other);
    // TODO: we should just return a concatenated list, not change ourselves
    return this;
  }

  @override
  void add(mupdf.Page value) {
    _document.toPDFDocument()?.insertPage(-1, value.toPDFObject());
  }

  @override
  void addAll(Iterable<mupdf.Page> iterable) {
    mupdf.PDFDocument? pdf = _document.toPDFDocument();
    if (pdf != null) {
      for (mupdf.Page page in iterable) {
        add(page);
      }
    }
  }

  @override
  mupdf.Page operator [](int index) {
    return _document.loadPage(index, index);
  }

  @override
  mupdf.Page elementAt(int index) {
    return this[index];
  }

  @override
  void operator []=(int index, mupdf.Page value) {
    _document.toPDFDocument()?.replacePage(index, value.toPDFPage());
  }

  @override
  bool any(bool Function(mupdf.Page element) test) {
    for (mupdf.Page page in this) {
      if (test(page)) {
        return true;
      }
    }
    return false;
  }

  @override
  bool every(bool Function(mupdf.Page element) test) {
    for (mupdf.Page page in this) {
      if (!test(page)) {
        return false;
      }
    }
    return true;
  }

  @override
  Map<int, mupdf.Page> asMap() {
    Map<int, mupdf.Page> map = {};
    for (int i = 0; i < length; i++) {
      map[i] = this[i];
    }
    return map;
  }

  @override
  List<R> cast<R>() {
    List<R> casted = [];
    for (mupdf.Page page in this) {
      // TODO: check if this actually works
      if (R is JObjType<Never>) {
        casted.add(page.castTo(R as JObjType<Never>));
      } else {
        casted.add(page as R);
      }
    }
    return casted;
  }

  @override
  void clear() {
    mupdf.PDFDocument? pdf = _document.toPDFDocument();
    if (pdf != null) {
      int oldLength = length;
      for (int i = 0; i < oldLength; i++) {
        pdf.deletePage(0);
      }
    }
  }

  @override
  bool contains(Object? element) {
    for (mupdf.Page page in this) {
      if (element == page) {
        return true;
      }
    }
    return false;
  }

  @override
  Iterable<T> expand<T>(Iterable<T> Function(mupdf.Page element) toElements) {
    List<T> result = [];
    for (mupdf.Page page in this) {
      result.addAll(toElements(page));
    }
    return result;
  }

  @override
  void fillRange(int start, int end, [mupdf.Page? fillValue]) {
    for (int i = start; i < end; i++) {
      if (fillValue != null) {
        this[i] = fillValue;
      } // FIXME: what if fillValue is null?
    }
  }

  @override
  mupdf.Page firstWhere(bool Function(mupdf.Page element) test,
      {mupdf.Page Function()? orElse}) {
    for (mupdf.Page page in this) {
      if (test(page)) {
        return page;
      }
    }
    if (orElse == null) {
      throw StateError("firstWhere: no page matched and orElse is null");
    } else {
      return orElse();
    }
  }

  @override
  T fold<T>(
      T initialValue, T Function(T previousValue, mupdf.Page element) combine) {
    // TODO: implement fold
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> followedBy(Iterable<mupdf.Page> other) {
    return this + other.toList();
  }

  @override
  void forEach(void Function(mupdf.Page element) action) {
    for (mupdf.Page page in this) {
      action(page);
    }
  }

  @override
  Iterable<mupdf.Page> getRange(int start, int end) {
    List<mupdf.Page> pages = [];
    for (int i = start; i < end; i++) {
      pages.add(this[i]);
    }
    return pages;
  }

  @override
  int indexOf(mupdf.Page element, [int start = 0]) {
    for (int i = start; i < length; i++) {
      if (this[i] == element) {
        return i;
      }
    }
    return -1;
  }

  @override
  int indexWhere(bool Function(mupdf.Page element) test, [int start = 0]) {
    for (int i = start; i < length; i++) {
      if (test(this[i])) {
        return i;
      }
    }
    return -1;
  }

  @override
  void insert(int index, mupdf.Page element) {
    this[index] = element;
  }

  @override
  void insertAll(int index, Iterable<mupdf.Page> iterable) {
    // TODO: implement insertAll
  }

  @override
  bool get isEmpty => length <= 0;

  @override
  bool get isNotEmpty => !isEmpty;

  @override
  DocumentPagesIterator get iterator {
    return DocumentPagesIterator(this);
  }

  @override
  String join([String separator = ""]) {
    // TODO: implement join
    throw UnimplementedError();
  }

  @override
  int lastIndexOf(mupdf.Page element, [int? start]) {
    int i = this.reversed.toList().indexOf(element, start ?? 0);
    return i == -1 ? i : length - 1 - i;
  }

  @override
  int lastIndexWhere(bool Function(mupdf.Page element) test, [int? start]) {
    int i = this.reversed.toList().indexWhere(test, start ?? 0);
    return i == -1 ? i : length - 1 - i;
  }

  @override
  mupdf.Page lastWhere(bool Function(mupdf.Page element) test,
      {mupdf.Page Function()? orElse}) {
    return this.reversed.toList().firstWhere(test, orElse: orElse);
  }

  @override
  Iterable<T> map<T>(T Function(mupdf.Page e) toElement) {
    List<T> result = [];
    for (mupdf.Page page in this) {
      result.add(toElement(page));
    }
    return result;
  }

  @override
  mupdf.Page reduce(
      mupdf.Page Function(mupdf.Page value, mupdf.Page element) combine) {
    // TODO: implement reduce
    throw UnimplementedError();
  }

  @override
  bool remove(Object? value) {
    int index = _document.findPageIndex(value as mupdf.Page);
    if (index == -1) {
      return false;
    } else {
      removeAt(index);
      return true;
    }
  }

  @override
  mupdf.Page removeAt(int index) {
    mupdf.Page page = this[index];
    _document.toPDFDocument()?.deletePage(index);
    return page;
  }

  @override
  mupdf.Page removeLast() {
    return removeAt(length - 1);
  }

  @override
  void removeRange(int start, int end) {
    // TODO: implement removeRange
  }

  @override
  void removeWhere(bool Function(mupdf.Page element) test) {
    // TODO: implement removeWhere
  }

  @override
  void replaceRange(int start, int end, Iterable<mupdf.Page> replacements) {
    // TODO: implement replaceRange
  }

  @override
  void retainWhere(bool Function(mupdf.Page element) test) {
    // TODO: implement retainWhere
  }

  @override
  Iterable<mupdf.Page> get reversed {
    iterator.reverse();
    List<mupdf.Page> pages = [];
    while (iterator.moveNext()) {
      pages.add(iterator.current);
    }
    return pages;
  }

  @override
  void setAll(int index, Iterable<mupdf.Page> iterable) {
    for (mupdf.Page page in iterable) {
      this[index] = page;
    }
  }

  @override
  void setRange(int start, int end, Iterable<mupdf.Page> iterable,
      [int skipCount = 0]) {
    // TODO: implement setRange
  }

  @override
  void shuffle([Random? random]) {
    // TODO: implement shuffle
  }

  @override
  // TODO: implement single
  mupdf.Page get single => throw UnimplementedError();

  @override
  mupdf.Page singleWhere(bool Function(mupdf.Page element) test,
      {mupdf.Page Function()? orElse}) {
    // TODO: implement singleWhere
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> skip(int count) {
    // TODO: implement skip
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> skipWhile(bool Function(mupdf.Page value) test) {
    // TODO: implement skipWhile
    throw UnimplementedError();
  }

  @override
  void sort([int Function(mupdf.Page a, mupdf.Page b)? compare]) {
    // TODO: implement sort
  }

  @override
  List<mupdf.Page> sublist(int start, [int? end]) {
    // TODO: implement sublist
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> take(int count) {
    // TODO: implement take
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> takeWhile(bool Function(mupdf.Page value) test) {
    // TODO: implement takeWhile
    throw UnimplementedError();
  }

  @override
  List<mupdf.Page> toList({bool growable = true}) {
    return this;
  }

  @override
  Set<mupdf.Page> toSet() {
    // TODO: implement toSet
    throw UnimplementedError();
  }

  @override
  Iterable<mupdf.Page> where(bool Function(mupdf.Page element) test) {
    // TODO: implement where
    throw UnimplementedError();
  }

  @override
  Iterable<T> whereType<T>() {
    // TODO: implement whereType
    throw UnimplementedError();
  }
}
