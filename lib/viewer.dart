part of viewer3d;

class Viewer extends StatefulWidget {
  const Viewer({Key? key}) : super(key: key);

  @override
  _ViewerState createState() => _ViewerState();
}

class _ViewerState extends State<Viewer> {
  final String viewType = 'com.ayoub.viewer_3d';
  final Map<String, dynamic> creationParams = <String, dynamic>{};
  late int id;

  void _onPlatformViewCreated(int id) {
    id = id;
    Viewer3d._initChannel();
  }

  @override
  Widget build(BuildContext context) {
    return AndroidView(
      viewType: viewType,
      onPlatformViewCreated: _onPlatformViewCreated,
      layoutDirection: TextDirection.ltr,
      creationParams: creationParams,
      creationParamsCodec: const StandardMessageCodec(),
    );
  }
}
