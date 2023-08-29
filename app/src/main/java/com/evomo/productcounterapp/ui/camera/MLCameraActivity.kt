package com.evomo.productcounterapp.ui.camera

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.evomo.productcounterapp.R
import com.evomo.productcounterapp.ml.SsdMobilenetV11Metadata1
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.lang.Math.abs
import kotlin.math.sqrt

// Define a simple TrackedObject class
data class TrackedObject(var centerX: Float, var centerY: Float, var age: Int = 1)

class MLCameraActivity : AppCompatActivity() {

    lateinit var labels:List<String>
    var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    val paint = Paint()
    lateinit var imageProcessor: ImageProcessor
    lateinit var bitmap:Bitmap
    lateinit var imageView: ImageView
    lateinit var cameraDevice: CameraDevice
    lateinit var handler: Handler
    lateinit var cameraManager: CameraManager // To open camera
    lateinit var textureView: TextureView // To show video stream
    lateinit var model: SsdMobilenetV11Metadata1

    var objectCount = 0
    private var objectIdCounter = 0
    private val trackedObjects = mutableMapOf<Int, TrackedObject>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mlcamera)
        OpenCVLoader.initDebug()
        get_permission()

        labels = FileUtil.loadLabels(this, "labels.txt")
        imageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()
        model = SsdMobilenetV11Metadata1.newInstance(this)
        val handlerThread = HandlerThread("videoThread")
        handlerThread.start()
        handler = Handler(handlerThread.looper)

        imageView = findViewById(R.id.imageView)

        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = object:TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureAvailable(p0: SurfaceTexture, p1: Int, p2: Int) {
                open_camera()
            }
            override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {
            }
            override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                return false
            }

            override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                bitmap = textureView.bitmap!!
                var image = TensorImage.fromBitmap(bitmap)
                image = imageProcessor.process(image)

                val outputs = model.process(image)
                val locations = outputs.locationsAsTensorBuffer.floatArray
                val classes = outputs.classesAsTensorBuffer.floatArray
                val scores = outputs.scoresAsTensorBuffer.floatArray
                val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

                var mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutable)

                val h = mutable.height
                val w = mutable.width
                paint.textSize = h/15f
                paint.strokeWidth = h/85f
                var x = 0

                val detectedObjects = mutableListOf<Triple<Float, Float, Int>>()
                val targetClassIndex = labels.indexOf("car")

                Log.d("object_location", "Locations: ${locations.joinToString(", ")}")

                scores.forEachIndexed { index, fl ->
                    x = index
                    x *= 4
                    if (fl > 0.5 && classes.get(index).toInt() == targetClassIndex) {
                        // Calculate rectangle coordinates
                        val left = locations[x + 1] * w
                        val top = locations[x] * h
                        val right = locations[x + 3] * w
                        val bottom = locations[x + 2] * h

                        // Calculate rectangle center
                        val centerX = (left + right) / 2
                        val centerY = (top + bottom) / 2

                        // Calculate dot radius
                        val dotRadius = 5f

                        paint.setColor(colors.get(index))
                        paint.style = Paint.Style.STROKE

                        // Bounding boxes
                        canvas.drawRect(RectF(left, top, right, bottom), paint)
                        // Centroid
                        canvas.drawCircle(centerX, centerY, dotRadius, paint)

                        paint.style = Paint.Style.FILL
                        canvas.drawText(labels.get(classes.get(index).toInt())+" "+fl.toString(), locations.get(x+1)*w, locations.get(x)*h, paint)

                        // Assign a unique ID to each detected object
                        val objectId = objectIdCounter++
                        detectedObjects.add(Triple(centerX, centerY, objectId))

                        // Display the assigned object ID
                        paint.style = Paint.Style.FILL
                        val objectIdText = "ID: $objectId"
                        val textWidth = paint.measureText(objectIdText)
                        canvas.drawText(objectIdText, centerX - textWidth / 2, centerY, paint)
                    }
                }

                // Loop through detected objects to see if they crossed the line
                val lineX = mutable.width / 2 // X-coordinate of the vertical line
                val crossingThreshold = 100 // Adjust this threshold as needed

                detectedObjects.removeAll { (centroid, _) ->
                    val crossedLine = abs(centroid - lineX) < crossingThreshold

                    Log.d("ObjectDetection", "Object X: $centroid, Distance to Line: ${abs(centroid - lineX)}")

                    if (crossedLine) {
                        objectCount++
                    }
                    crossedLine
                }

                drawVerticalLine(mutable, lineX, Color.RED)

                Log.d("test_count_obj", objectCount.toString())

                val counterStr = resources.getString(R.string.counted_object, objectCount.toString())

                val text = findViewById<View>(R.id.countText) as TextView
                text.setText(counterStr)

                imageView.setImageBitmap(mutable)

                val trackedObjectsToRemove = mutableListOf<Int>()
                var someThreshold = 1

                // Update the tracked objects
                trackedObjects.forEach { (objectId, trackedObject) ->
                    // Check if the object is still detected
                    val matchedObject = detectedObjects.find { (centerX, centerY, _) ->
                        // Compare centroids or other relevant attributes
                        // You might need to fine-tune the threshold here
                        abs(trackedObject.centerX - centerX) < someThreshold &&
                                abs(trackedObject.centerY - centerY) < someThreshold
                    }

                    if (matchedObject != null) {
                        // Update the tracked object's centroid
                        trackedObject.centerX = matchedObject.first
                        trackedObject.centerY = matchedObject.second
                        trackedObject.age++
                    } else {
                        // If the object wasn't detected, mark it for removal
                        trackedObjectsToRemove.add(objectId)
                    }
                }

                // Remove objects that haven't been detected for a while
                trackedObjectsToRemove.forEach { objectId ->
                    trackedObjects.remove(objectId)
                }

                // Add newly detected objects to the tracking map
                detectedObjects.forEach { (centerX, centerY, objectId) ->
                    if (!trackedObjects.containsKey(objectId)) {
                        trackedObjects[objectId] = TrackedObject(centerX, centerY)
                    }
                }

            }
        }

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    @SuppressLint("MissingPermission")
    fun open_camera(){
        cameraManager.openCamera(cameraManager.cameraIdList[0], object:CameraDevice.StateCallback(){
            override fun onOpened(p0: CameraDevice) {
                cameraDevice = p0

                var surfaceTexture = textureView.surfaceTexture
                var surface = Surface(surfaceTexture)

                var captureRequest = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
                captureRequest.addTarget(surface)

                cameraDevice.createCaptureSession(listOf(surface), object: CameraCaptureSession.StateCallback(){
                    override fun onConfigured(p0: CameraCaptureSession) {
                        p0.setRepeatingRequest(captureRequest.build(), null, null)
                    }
                    override fun onConfigureFailed(p0: CameraCaptureSession) {
                    }
                }, handler)
            }

            override fun onDisconnected(p0: CameraDevice) {

            }

            override fun onError(p0: CameraDevice, p1: Int) {

            }
        }, handler)
    }

    fun drawVerticalLine(bitmap: Bitmap, x: Int, lineColor: Int) {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        var color = Scalar(255.0, 0.0, 0.0) // Default color (BGR format)

        val startPoint = Point(x.toDouble(), 0.0)
        val endPoint = Point(x.toDouble(), mat.height().toDouble())

        Imgproc.line(mat, startPoint, endPoint, color, 4)

        Utils.matToBitmap(mat, bitmap)
    }

    // Checking and getting camera permission from user's device
    fun get_permission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 101)
        }
    }

    // Granting permission from user to use camera
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            get_permission()
        }
    }
}