package com.ayoub.viewer_3d
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel


class Viewer3dPlugin: FlutterPlugin {

  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "viewer_3d")
    flutterPluginBinding
            .platformViewRegistry
            .registerViewFactory("com.ayoub.viewer_3d", NativeViewFactory(channel))
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
