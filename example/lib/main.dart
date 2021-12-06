import 'package:flutter/material.dart';
import 'dart:math' as math;
import 'package:viewer_3d/viewer_3d.dart';

import 'package:vector_math/vector_math.dart' show Vector3;

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  double roationValue = 0.0;

  Vector3 camPos = Vector3.zero();
  late Viewer3DController viewer3dCtl;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: Stack(
            children: [
              Viewer3D(
                onViewCreated: (ctl) {
                  viewer3dCtl = ctl;
                },
              ),
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
            ],
          )),
    );
  }
}
