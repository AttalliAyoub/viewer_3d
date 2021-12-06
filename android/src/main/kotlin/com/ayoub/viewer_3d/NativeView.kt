package com.ayoub.viewer_3d

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
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
    MethodChannel.MethodCallHandler {
    // private val flutterPluginBinding = flutterPluginBinding
    private val channel = MethodChannel(flutterPluginBinding.binaryMessenger, "viewer_3d")
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
            "loadFile" -> {
                loadModel(call.argument<String>("uri")!!, result)
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

    private fun loadModel(uri: String, result: MethodChannel.Result) {
        try {
            val loader = LoaderOBJ(this, uri)
            loader.parse()
            currentScene.clearChildren()
            mObject = loader.parsedObject
            val material = Material()
            material.enableLighting(true)
            material.diffuseMethod = DiffuseMethod.Lambert()
            material.color = Color.RED
            material.colorInfluence = 1f
            // mObject.material = material
            mObject.position = Vector3.ZERO
            val box = mObject.boundingBox.max
            currentCamera.position = Vector3(0.0, box.y / 2, box.z * 8)
            currentScene.addChild(mObject)
            result.success(null)
            /*
            val data = mapOf(
                    "max" to mapOf(
                            "x" to mObject.boundingBox.max.x,
                            "y" to mObject.boundingBox.max.y,
                            "z" to mObject.boundingBox.max.z
                    ),
                    "min" to mapOf(
                            "x" to mObject.boundingBox.min.x,
                            "x" to mObject.boundingBox.min.y,
                            "x" to mObject.boundingBox.min.z
                    )
            )
            Log.w("data", data.toString())
            */
        } catch (error: Throwable) {
            result.error("${error.hashCode()}", error.message, error.localizedMessage)
        }
    }

    fun loadEarth() {
        val earthMaterial = Material()
        earthMaterial.enableLighting(true)
        earthMaterial.diffuseMethod = DiffuseMethod.Lambert()
        earthMaterial.colorInfluence = 0f
        /*
        val path = flLoader.getLookupKeyForAsset("assets/earthtruecolor_nasa_big.jpg")

        val earthImage = BitmapFactory.decodeStream(context.assets.open(path))
        val earthTexture = Texture("Earth", earthImage)
        try {
            earthMaterial.addTexture(earthTexture)
        } catch (error: TextureException) {
            Log.d("BasicRenderer.initScene", error.toString())
        }
         */
        mObject = Sphere(1f, 24, 24)
        mObject.material = earthMaterial
        mObject.position = Vector3.ZERO
        currentScene.addChild(mObject)
    }


}
