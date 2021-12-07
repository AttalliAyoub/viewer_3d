import 'dart:async';
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:path/path.dart' as path;
import 'package:archive/archive.dart';
import 'package:archive/archive_io.dart';
// import 'package:flutter_archive/flutter_archive.dart';

import 'package:path_provider/path_provider.dart' as path_provider;

class Download {
  Download._();
  static final dio = Dio();
  static const _url =
      "https://raw.githubusercontent.com/AttalliAyoub/viewer_3d/main/assets/assets.zip";
  // "https://nodejs.org/dist/v16.13.1/node-v16.13.1-win-x64.zip";
  static Future<Directory> get dir => path_provider.getTemporaryDirectory();
  static Future<String> get savePath async =>
      path.join(await dir.then((d) => d.path), 'assets.zip');
  static Future<Directory> get assetsDir =>
      dir.then((d) => Directory(path.join(d.path, 'assets')));
  static Future<bool> get exisit => assetsDir.then((d) => d.exists());

  static Future<void> clear() async {
    await savePath.then((p) async {
      final f = File(p);
      if (await f.exists()) return f.delete();
    });
    await assetsDir.then((d) async {
      if (await d.exists()) return d.delete(recursive: true);
    });
  }

  static Future<void> download({
    ProgressCallback? onReceiveProgress,
    CancelToken? cancelToken,
  }) async {
    await dio.download(
      _url,
      await savePath,
      onReceiveProgress: onReceiveProgress,
      deleteOnError: true,
      cancelToken: cancelToken,
    );
  }

  static Future<void> extractToDirectory() async {
    final zipFile = await savePath.then((p) => File(p).readAsBytes());
    final destinationDir = await assetsDir;
    final archive = ZipDecoder().decodeBytes(zipFile);
    for (final file in archive.files) {
      if (!file.isCompressed) continue;
      final filePath = path.join(destinationDir.path, file.name);
      final outFile = await File(filePath).create(recursive: true);
      await outFile.writeAsBytes(file.content);
    }
    // return ZipFile.extractToDirectory(
    //   zipFile: zipFile,
    //   destinationDir: destinationDir,
    //   onExtracting: (zipEntry, progress) {
    //     if (onExtracting != null) {
    //       onExtracting(progress);
    //     }
    //     return ZipFileOperation.includeItem;
    //   },
    // );
  }
}
