// import 'dart:async';
library viewer3d;

import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:vector_math/vector_math.dart';
// export 'package:vector_math/vector_math.dart';
part 'viewer.dart';

extension MyVector3 on Vector3 {
  Map<String, double> get json => {'x': x, 'y': y, 'z': z};
  Map<String, String> get shortJson => {'x': shortX, 'y': shortY, 'z': shortZ};
  String get shortX => x.toStringAsFixed(2);
  String get shortY => y.toStringAsFixed(2);
  String get shortZ => z.toStringAsFixed(2);
}

class Viewer3d {
  Viewer3d._();
  static const MethodChannel _channel = MethodChannel('viewer_3d');

  static void _initChannel() {
    _channel.setMethodCallHandler(_methodCallHandler);
  }

  static Future<dynamic> _methodCallHandler(MethodCall call) {
    debugPrint(call.method);
    return Future.value();
  }

  static Future<void> loadFile(String uri) {
    return _channel.invokeMethod<void>('loadFile', {'uri': uri});
  }

  static Future<void> rotate(Vector3 rotation) {
    return _channel.invokeMethod('rotate', rotation.json);
  }

  static Future<void> moveCam(Vector3 rotation) {
    return _channel.invokeMethod('moveCam', rotation.json);
  }

  static Future<double> get getRotation {
    return _channel
        .invokeMethod<double>('getRotation')
        .then((value) => value ?? 0.0);
  }
}
