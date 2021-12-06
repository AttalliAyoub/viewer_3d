package com.ayoub.viewer_3d
import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory


class Viewer3dPlugin: FlutterPlugin, PlatformViewFactory(StandardMessageCodec.INSTANCE) {

  private lateinit var flutterPluginBinding : FlutterPlugin.FlutterPluginBinding

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    // channel = MethodChannel(flutterPluginBinding.binaryMessenger, "viewer_3d")
    this.flutterPluginBinding = flutterPluginBinding
    flutterPluginBinding
    flutterPluginBinding
            .platformViewRegistry
            .registerViewFactory("com.ayoub.viewer_3d", this)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    // channel.setMethodCallHandler(null)
  }

  override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
    val creationParams = args as Map<String?, Any?>?
    return NativeView(context, flutterPluginBinding, viewId, creationParams)
  }
}
