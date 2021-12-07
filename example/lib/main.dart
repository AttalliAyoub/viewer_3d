import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:path/path.dart' as path;
import 'dart:math' as math;
import 'package:viewer_3d/viewer_3d.dart';
import 'package:vector_math/vector_math.dart' show Vector3;
import 'package:viewer_3d_example/donload.dart';

void main() {
  runApp(
    const MaterialApp(
      debugShowCheckedModeBanner: false,
      home: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
    download();
  }

  void _showMessage({
    required String message,
    String label = 'OK',
    VoidCallback? onPressed,
  }) {
    onPressed ??= () {};
    final snackBar = SnackBar(
      content: Text(message),
      action: SnackBarAction(label: label, onPressed: onPressed),
    );
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }

  double roationValue = 0.0;
  Vector3 camPos = Vector3.zero();
  late Viewer3DController viewer3dCtl;

  void refresh() async {
    await Download.clear();
    download();
  }

  void download() async {
    if (await Download.exisit) {
      _showMessage(
        message: 'your assets is Downloaded',
        label: 'Refresh',
        onPressed: refresh,
      );
      return;
    }
    final cancelToken = CancelToken();
    try {
      await Download.download(
        cancelToken: cancelToken,
        onReceiveProgress: (count, total) {
          _showMessage(
            message:
                "Downloading - ${(100 * count / total).toStringAsFixed(2)}",
            label: 'Cancel',
            onPressed: cancelToken.cancel,
          );
        },
      );
      _showMessage(
          message:
              "Download completed\nClick in refresh button to force download");
    } catch (err) {
      debugPrint('$err');
      _showMessage(message: "Download Error: ${err.toString()}");
      return;
    }
    try {
      await Download.extractToDirectory();
      _showMessage(
          message:
              "Extracting completed\nClick in refresh button to force download");
    } catch (err) {
      _showMessage(message: "Extract Error: ${err.toString()}");
    }
  }

  void pickFile() async {
    final list = await Download.assetsDir.then((d) => d.listSync());

    showDialog(
        context: context,
        builder: (context) {
          return SimpleDialog(
            title: const Text('Load a file'),
            // children: dir.listSync().map((file) {
            //   return ListTile(
            //     title: Text(path.basename(file.path)),
            //     subtitle: Text(file.path),
            //   );
            // }).toList(),
            children: [
              for (final file in list)
                ListTile(
                  title: Text(path.basename(file.path)),
                  subtitle: Text(file.path),
                  onTap: () async {
                    try {
                      await viewer3dCtl.loadModel(Model(path: file.path));
                      _showMessage(message: 'earth loaded');
                    } catch (err) {
                      _showMessage(message: '$err');
                    }
                    Navigator.of(context).pop();
                  },
                ),
              ListTile(
                title: const Text('load a sphere'),
                subtitle: const Text('sphere'),
                onTap: () async {
                  try {
                    await viewer3dCtl.loadEarth();
                    _showMessage(message: 'earth loaded');
                  } catch (err) {
                    _showMessage(message: '$err');
                  }
                  Navigator.of(context).pop();
                },
              ),
            ],
          );
        });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        floatingActionButton: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            FloatingActionButton(
              child: const Icon(Icons.refresh),
              onPressed: () {
                _showMessage(
                    message: 'Are you shure you want to refresh',
                    label: 'Refresh',
                    onPressed: refresh);
              },
            ),
            const SizedBox(width: 10),
            FloatingActionButton(
              child: const Icon(Icons.upload_file),
              onPressed: pickFile,
            ),
          ],
        ),
        body: Stack(
          children: [
            Viewer3D(
              onViewCreated: (ctl) {
                viewer3dCtl = ctl;
              },
            ),
            /*
              Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Text(
                      'Cam Pos: ${camPos.shortJson}',
                      style: const TextStyle(color: Colors.red),
                    ),
                    Text(
                      'Obj rotation: ${roationValue.toStringAsFixed(2)}',
                      style: const TextStyle(color: Colors.red),
                    )
                  ],
                ),
              ),
              Positioned(
                top: 20,
                right: 20,
                child: TextButton(
                  child: Text('loadFile $roationValue'),
                  onPressed: () async {
                    viewer3dCtl.loadFile('assets/T-shirt_3dmodel.obj');
                  },
                ),
              ),

              Positioned(
                bottom: 20,
                left: 20,
                right: 20,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Slider(
                      min: -360,
                      max: 360,
                      value: camPos.x,
                      label: 'Cam x',
                      onChanged: (double value) {
                        setState(() {
                          camPos.x = value;
                        });
                        viewer3dCtl.moveCam(camPos);
                      },
                    ),
                    Slider(
                      min: -360,
                      max: 360,
                      value: camPos.y,
                      label: 'Cam y',
                      onChanged: (double value) {
                        setState(() {
                          camPos.y = value;
                        });
                        viewer3dCtl.moveCam(camPos);
                      },
                    ),
                    Slider(
                      min: -360,
                      max: 360,
                      value: camPos.z,
                      label: 'Cam z',
                      onChanged: (double value) {
                        setState(() {
                          camPos.z = value;
                        });
                        debugPrint('${camPos.z}');
                        viewer3dCtl.moveCam(camPos);
                      },
                    ),
                    Slider(
                      min: -360,
                      max: 360,
                      value: roationValue,
                      label: 'object y rote',
                      onChanged: (double value) {
                        setState(() {
                          roationValue = value;
                        });
                        viewer3dCtl.rotate(Vector3(0, value, 0));
                      },
                    ),
                  ],
                ),
              ),
              */
          ],
        ));
  }
}
