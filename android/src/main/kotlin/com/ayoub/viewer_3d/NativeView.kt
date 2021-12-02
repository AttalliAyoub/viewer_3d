package com.ayoub.viewer_3d

import android.content.Context
import android.view.View
import androidx.annotation.NonNull
import com.ayoub.viewer_3d.engine.BasicRenderer
import io.flutter.FlutterInjector
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView


internal class NativeView(context: Context,
                          id: Int,
                          channel : MethodChannel,
                          creationParams: Map<String?, Any?>?) :
        PlatformView, ActivityAware {
    private val surfaceView = SurfaceView(context)
    private val renderer = BasicRenderer(context, channel)
    private val id: Int = id
    override fun getView(): View {
        return surfaceView
    }

    override fun dispose() {
        // surfaceView.distra
        // renderer.onPause()
    }

    init {
        surfaceView.setFrameRate(60.0)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY
        surfaceView.setSurfaceRenderer(renderer)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY

    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {}

    override fun onDetachedFromActivityForConfigChanges() {
        renderer.onPause()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        renderer.onResume()
    }

    override fun onDetachedFromActivity() {
    }
}
