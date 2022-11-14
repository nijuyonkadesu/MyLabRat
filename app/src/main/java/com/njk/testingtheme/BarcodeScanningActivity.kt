package com.njk.testingtheme

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.njk.testingtheme.databinding.FragmentBarcodeBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// emit values - temporary
typealias BarcodeListener = (code: String) -> Unit
const val TAG = "QR"
lateinit var binding: FragmentBarcodeBinding

class BarcodeScanningActivity: AppCompatActivity() {

    // CameraX
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    // Inflate layout
//    private lateinit var binding: FragmentBarcodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBarcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: connect with navigation
        cameraExecutor = Executors.newSingleThreadExecutor()
        startCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            // some internal function to work with takePicture()
            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                val barcodeAnalyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST)
                    .setImageQueueDepth(1)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { code ->
                            Log.d(TAG, "QR: $code")
                        })
                    }

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture, barcodeAnalyzer)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
        // returns executioner that runs on Main Thread
    }
}

// Trying to get Barcode Scanner
private class BarcodeAnalyzer(private val listener: BarcodeListener) : ImageAnalysis.Analyzer {

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Pass image to an ML Kit Vision API
            // Barcode Options
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_QR_CODE
                )
                .build()

            // [START get_detector]
            val scanner = BarcodeScanning.getClient()

            // [START run_detector]
            val resultSet = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    for (barcode in barcodes) {
//                        val bounds = barcode.boundingBox
//                        val corners = barcode.cornerPoints
//
//                        val rawValue = barcode.rawValue
//
//                        val valueType = barcode.valueType
                        // See API reference for complete list of supported types
//                        when (valueType) {
//                            Barcode.FORMAT_QR_CODE -> {
                                val rawText = barcode.rawValue ?: "not ok"
                                binding.qr.text = rawText
                                listener(rawText)
//                            }
//                        }
                    }
                }
                .addOnFailureListener {
                    val msg = "fail QR"
                    binding.qr.text = msg
                    Log.e(TAG, "QR fail")
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
