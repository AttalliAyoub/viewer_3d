package com.ayoub.viewer_3d

import android.content.Context
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class NativeViewFactory(channel : MethodChannel) : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private val _channel = channel
    override fun create(context: Context, viewId: Int, args: Any?): PlatformView {
        val creationParams = args as Map<String?, Any?>?
        return NativeView(context, viewId, _channel, creationParams)
    }
}
