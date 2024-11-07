package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.local.entity.CancerEntity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.view.history.HistoryFactory
import com.dicoding.asclepius.view.history.HistoryViewModel

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var historyViewModel: HistoryViewModel
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory = HistoryFactory.getFactoryInstance(this)
        historyViewModel = ViewModelProvider(this, factory)[HistoryViewModel::class.java]

        binding.arrowBack.setOnClickListener {
            onBackPressed()
        }

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri: Uri? = intent.getParcelableExtra("IMAGE_URI")
        val resultText: String? = intent.getStringExtra("RESULT_TEXT")

        imageUri?.let {
            binding.resultImage.setImageURI(it)
        }

        resultText?.let {
            binding.resultText.text = it
        }

        binding.btnSave.setOnClickListener {
            if (!isSaved) {
                imageUri?.let { uri ->
                    resultText?.let { result ->
                        val cancerEntity = CancerEntity(
                            image = uri.toString(),
                            result = result
                        )

                        historyViewModel.insertCancers(listOf(cancerEntity))
                        isSaved = true

                        Toast.makeText(this, "Saved Successfully", Toast.LENGTH_SHORT).show()

                        binding.btnSave.isEnabled = false
                    }
                }
            }
        }
    }
}