// import 'dart:async';
library viewer3d;

import 'package:flutter/services.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/widgets.dart';
import 'package:vector_math/vector_math.dart';
// export 'package:vector_math/vector_math.dart';

part 'controller.dart';
part 'model.dart';

typedef ViewCreatedCallBack = void Function(Viewer3DController controller);

class Viewer3D extends StatefulWidget {
  final ViewCreatedCallBack? onViewCreated;
  final Model? initialModel;
  const Viewer3D({
    Key? key,
    this.initialModel,
    this.onViewCreated,
  }) : super(key: key);

  @override
  _Viewer3DState createState() => _Viewer3DState();
}

class _Viewer3DState extends State<Viewer3D> {
  final String viewType = 'com.ayoub.viewer_3d';
  final creationParams = <String, dynamic>{};
  late int id;
  late Viewer3DController controller;

  @override
  void initState() {
    super.initState();
    creationParams['initialModel'] = widget.initialModel?.json;
  }

  void _onPlatformViewCreated(int id) {
    id = id;
    controller = Viewer3DController._(id: id, model: widget.initialModel);
    if (widget.onViewCreated != null) widget.onViewCreated!(controller);
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
