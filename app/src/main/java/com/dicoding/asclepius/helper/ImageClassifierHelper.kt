package com.dicoding.asclepius.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassifierHelper(private val context: Context) {

    private val modelPath = "cancer_classification.tflite"
    private lateinit var interpreter: Interpreter

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val mappedByteBuffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        interpreter = Interpreter(mappedByteBuffer)
    }

    fun classifyStaticImage(imageUri: Uri): Pair<String, Float>? {
        val bitmap = loadImageFromUri(imageUri) ?: return null
        val inputBuffer = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(2) }

        interpreter.run(inputBuffer, output)

        val resultLabel = if (output[0][0] > output[0][1]) "Non-cancerous" else "Cancerous"
        val confidence = maxOf(output[0][0], output[0][1]) * 100
        return Pair(resultLabel, confidence)
    }

    private fun loadImageFromUri(uri: Uri): Bitmap? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(224 * 224)
        resizedBitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224)

        for (pixel in pixels) {
            byteBuffer.putFloat(((pixel shr 16 and 0xFF) - 127) / 128.0f)
            byteBuffer.putFloat(((pixel shr 8 and 0xFF) - 127) / 128.0f)
            byteBuffer.putFloat(((pixel and 0xFF) - 127) / 128.0f)
        }
        return byteBuffer
    }
}
