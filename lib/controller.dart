part of viewer3d;

extension MyVector3 on Vector3 {
  Map<String, double> get json => {'x': x, 'y': y, 'z': z};
  Map<String, String> get shortJson => {'x': shortX, 'y': shortY, 'z': shortZ};
  String get shortX => x.toStringAsFixed(2);
  String get shortY => y.toStringAsFixed(2);
  String get shortZ => z.toStringAsFixed(2);
}

class Viewer3DController {
  Model? _model;
  Model? get model => _model;
  set model(Model? m) {
    _model = m;
    if (m != null) {
      _loadFile(m.path);
    }
  }

  final int id;
  final MethodChannel _channel;
  Viewer3DController._({
    required this.id,
    Model? model,
  })  : _channel = MethodChannel('viewer_3d$id'),
        _model = model;

  Future<void> _loadFile(String uri) {
    return _channel.invokeMethod<void>('loadFile', {'uri': uri});
  }

  Future<void> _rotate(Vector3 rotation) {
    return _channel.invokeMethod('rotate', rotation.json);
  }

  Future<void> _moveCam(Vector3 rotation) {
    return _channel.invokeMethod('moveCam', rotation.json);
  }

  Future<double> get _getRotation {
    return _channel
        .invokeMethod<double>('getRotation')
        .then((value) => value ?? 0.0);
  }
}
