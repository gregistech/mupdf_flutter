import 'mupdf_android.dart';

extension Debug on Rect {
  String toDebugString() {
    return "$x0, $y0, $x1, $y1";
  }
}
