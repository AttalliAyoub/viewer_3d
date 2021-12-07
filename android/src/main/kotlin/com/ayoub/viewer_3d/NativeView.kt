package com.ayoub.viewer_3d

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.loader.ALoader
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.loader.async.IAsyncLoaderCallback
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.materials.textures.Texture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView

import java.io.File

internal class NativeView(
    context: Context,
    flutterPluginBinding: FlutterPlugin.FlutterPluginBinding,
    id: Int,
    creationParams: Map<String?, Any?>?
) :
    Renderer(context),
    PlatformView,
    MethodChannel.MethodCallHandler
{
    // private val flutterPluginBinding = flutterPluginBinding
    private val channel = MethodChannel(flutterPluginBinding.binaryMessenger, "viewer_3d$id")
    private val surfaceView = SurfaceView(context)
    private val id: Int = id
    override fun getView(): View {
        return surfaceView
    }

    init {
        channel.setMethodCallHandler(this)
        setFrameRate(60);
        surfaceView.setFrameRate(60.0)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY
        surfaceView.setSurfaceRenderer(this)
        surfaceView.renderMode = ISurface.RENDERMODE_WHEN_DIRTY

    }

    override fun dispose() {
        channel.setMethodCallHandler(null)
        // surfaceView.distra
        // renderer.onPause()
    }

    override fun onOffsetsChanged(
        xOffset: Float,
        yOffset: Float,
        xOffsetStep: Float,
        yOffsetStep: Float,
        xPixelOffset: Int,
        yPixelOffset: Int
    ) {
    }

    override fun onTouchEvent(event: MotionEvent?) {}

    /*
    override fun onRender(elapsedRealtime: Long, deltaTime: Double) {
        super.onRender(elapsedRealtime, deltaTime)
    }
     */

    private lateinit var mObject: Object3D
    private lateinit var mDirectionalLight: DirectionalLight

    override fun initScene() {
        mDirectionalLight = DirectionalLight(1.0, .2, -1.0)
        mDirectionalLight.setColor(1.0f, 1.0f, 1.0f)
        mDirectionalLight.power = 2f
        currentScene.addLight(mDirectionalLight)
        currentCamera.z = 4.2
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "loadModel" -> {
                val path = call.argument<String>("path")!!
                val texture = call.argument<String>("texture")
                val loader = LoaderOBJ(this, File(path))
                loadModel(loader, texture, result)
            }
            "loadEarth" -> {
                loadEarth()
                result.success(null)
            }
            "rotate" -> {
                val x = call.argument<Double>("x")!!
                val y = call.argument<Double>("y")!!
                val z = call.argument<Double>("z")!!
                mObject.rotX = x
                mObject.rotY = y
                mObject.rotZ = z
                result.success(null)
            }
            "moveCam" -> {
                val x = call.argument<Double>("x")!!
                val y = call.argument<Double>("y")!!
                val z = call.argument<Double>("z")!!
                currentCamera.position = Vector3(x, y, z)
                result.success(null)
            }
            "getRotation" -> {
                result.success(mObject.rotY)
            }
            else -> result.notImplemented()
        }
    }

    private fun loadModel(loader: ALoader, texture: String?, result: MethodChannel.Result): ALoader {
        return super.loadModel(loader, object: IAsyncLoaderCallback {
            override fun onModelLoadComplete(loader: ALoader) {
                currentScene.clearChildren()
                mObject = (loader as LoaderOBJ).parsedObject
                mObject.material = loadTexture(texture)
                mObject.position = Vector3.ZERO
                val box = mObject.boundingBox.max
                currentCamera.position = Vector3(0.0, box.y / 2, box.z * 8)
                currentScene.addChild(mObject)
                result.success(null)
            }
            override fun onModelLoadFailed(loader: ALoader?) {
                result.error("CAN_NOT_LOAD", "Model load failed", "")
            }
        }, loader.tag)
    }

    private fun loadTexture(path: String? = null): Material {
        val material = Material()
        material.enableLighting(true)
        material.diffuseMethod = DiffuseMethod.Lambert()
        material.color = Color.RED
        material.colorInfluence = 0f
        if (path != null) {
            val image = BitmapFactory.decodeStream(context.assets.open(path))
            val texture = Texture("path", image)
            try {
                material.addTexture(texture)
            } catch (error: ATexture.TextureException) {
                Log.d("BasicRenderer.initScene", error.toString())
            }
        }
        return material
    }

    private fun loadEarth() {
        currentScene.clearChildren()
        mObject = Sphere(1f, 24, 24)
        mObject.material = loadTexture()
        mObject.position = Vector3.ZERO
        currentScene.addChild(mObject)
    }
}
