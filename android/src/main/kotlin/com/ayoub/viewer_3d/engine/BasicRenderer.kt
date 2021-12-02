package com.ayoub.viewer_3d.engine

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.annotation.NonNull
import io.flutter.FlutterInjector
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import org.rajawali3d.Object3D
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.loader.ALoader
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import java.io.File
import java.io.FileOutputStream


class BasicRenderer(context: Context, channel: MethodChannel) :
        Renderer(context),
        MethodChannel.MethodCallHandler {
    private val _channel = channel
    private val flLoader = FlutterInjector.instance().flutterLoader()

    private var rotate = true

    init {
        setFrameRate(60);
        _channel.setMethodCallHandler(this)
    }

    private lateinit var mObject: Object3D
    private lateinit var mDirectionalLight: DirectionalLight

    public override fun initScene() {
        mDirectionalLight = DirectionalLight(1.0, .2, -1.0)
        mDirectionalLight.setColor(1.0f, 1.0f, 1.0f)
        mDirectionalLight.power = 2f
        currentScene.addLight(mDirectionalLight)
        currentCamera.z = 4.2
    }

    /*
    fun loadEarth() {
        val earthMaterial = Material()
        earthMaterial.enableLighting(true)
        earthMaterial.diffuseMethod = DiffuseMethod.Lambert()
        earthMaterial.colorInfluence = 0f
        val path = flLoader.getLookupKeyForAsset("assets/earthtruecolor_nasa_big.jpg")
        val earthImage = BitmapFactory.decodeStream(context.assets.open(path))
        val earthTexture = Texture("Earth", earthImage)
        try {
            earthMaterial.addTexture(earthTexture)
        } catch (error: TextureException) {
            Log.d("BasicRenderer.initScene", error.toString())
        }
        mEarthSphere = Sphere(1f, 24, 24)
        mEarthSphere.material = earthMaterial
        mEarthSphere.position = Vector3.ZERO
        currentScene.addChild(mEarthSphere)
    }
     */

    override fun onRender(elapsedRealtime: Long, deltaTime: Double) {
        super.onRender(elapsedRealtime, deltaTime)
        /*
        if (rotate) {
            mEarthSphere.rotate(Vector3.Axis.Y, 1.0)
        }
        */
    }

    override fun onTouchEvent(event: MotionEvent) {

    }

    override fun onOffsetsChanged(x: Float, y: Float, z: Float, w: Float, i: Int, j: Int) {}

    private fun loadFile(assetKey: String): File {
        val key = flLoader.getLookupKeyForAsset(assetKey)
        val fileName = key.substring(key.lastIndexOf(File.separatorChar) + 1)
        val file = File(context.cacheDir, fileName)
        if (!file.exists()) {
            val input = context.assets.open(key)
            FileOutputStream(file).use { output ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            input.close();
        }
        return file
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

    private fun loadModel(
            uri: String,
            result: MethodChannel.Result
    ) {
        try {
            val loader = LoaderOBJ(this, loadFile(uri))
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
}