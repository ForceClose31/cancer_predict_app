package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("RESULT_IMAGE_URI")
        val predictionText = intent.getStringExtra("PREDICTION_TEXT") ?: "Prediction failed"
        val confidenceScore = intent.getFloatExtra("CONFIDENCE_SCORE", 0.0f)

        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }

        val resultText = "Prediction: $predictionText, Confidence: %.2f%%".format(confidenceScore)
        binding.resultText.text = resultText
    }
}
