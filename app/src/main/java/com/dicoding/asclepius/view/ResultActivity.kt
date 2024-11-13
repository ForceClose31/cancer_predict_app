package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(EXTRA_IMAGE_URI)
        val predictionText = intent.getStringExtra(EXTRA_PREDICTION) ?: "Prediction failed"

        val imageUri = imageUriString?.let { Uri.parse(it) }

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }
        val resultText = String.format("Prediction: %s", predictionText)

//        val resultText = "Prediction: $predictionText, Confidence: %.2f%%".format(confidenceScore * 100)
        binding.resultText.text = resultText
    }

    companion object {
        const val EXTRA_IMAGE_URI = "RESULT_IMAGE_URI"
        const val EXTRA_PREDICTION = "PREDICTION_TEXT"
        const val EXTRA_CONFIDENCE_SCORE = "CONFIDENCE_SCORE"
    }
}
