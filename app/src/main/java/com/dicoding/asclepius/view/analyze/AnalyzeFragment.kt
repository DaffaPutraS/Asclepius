package com.dicoding.asclepius.view.analyze

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.FragmentAnalyzeBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.view.ResultActivity
import com.yalantis.ucrop.UCrop

import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class AnalyzeFragment : Fragment() {

    private var _binding: FragmentAnalyzeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyzeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalyzeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        displaySelectedImage()

        binding.galleryButton.setOnClickListener {
            openImageGallery()
        }
        binding.analyzeButton.setOnClickListener {
            processImageAnalysis()
        }
    }

    private fun openImageGallery() {
        galleryImagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val galleryImagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.currentImageUri = uri
            initiateImageCrop(uri)
        } else {
            Log.d("Photo Picker", getString(R.string.tidak_ada_media_yang_dipilih))
            displayToastMessage(getString(R.string.tidak_ada_media_yang_dipilih))
        }
    }

    private fun displaySelectedImage() {
        viewModel.currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun processImageAnalysis() {
        viewModel.currentImageUri?.let { uri ->
            val imageClassifierHelper = ImageClassifierHelper(
                context = requireContext(),
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        displayToastMessage("Error: $error")
                    }

                    override fun onResult(results: List<Classifications>?, inferenceTime: Long) {
                        val classificationResultText = results?.joinToString("\n") { classification ->
                            classification.categories.joinToString(", ") {
                                "${it.label} ${"%.2f".format(it.score * 100)}%"
                            }
                        }
                        classificationResultText?.let {
                            moveToResult(uri, it)
                        } ?: displayToastMessage(getString(R.string.no_results_found))
                    }
                }
            )

            imageClassifierHelper.classifyStaticImage(uri)
        } ?: run {
            displayToastMessage(getString(R.string.select_an_image_first))
        }
    }

    private fun displayToastMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToResult(imageUri: Uri, resultText: String) {
        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("IMAGE_URI", imageUri)
            putExtra("RESULT_TEXT", resultText)
        }
        startActivity(intent)
    }

    private val cropResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                val data = result.data
                val resultUri = data?.let { UCrop.getOutput(it) }
                if (resultUri != null) {
                    viewModel.currentImageUri = resultUri
                    displaySelectedImage()
                } else {
                    displayToastMessage(getString(R.string.error_no_image_returned_after_cropping))
                }
            }
            Activity.RESULT_CANCELED -> {
                displaySelectedImage()
                displayToastMessage("Cropping is canceled.")
            }
            else -> {
                val cropError = UCrop.getError(result.data!!)
                cropError?.let {
                    displayToastMessage("Crop failed: ${it.message}")
                }
            }
        }
    }

    private fun initiateImageCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(
            File(
                requireContext().cacheDir,
                "cropped_${System.currentTimeMillis()}.jpg"
            )
        )
        val uCrop = UCrop.of(uri, destinationUri)
        uCrop.withAspectRatio(1f, 1f)
        uCrop.withMaxResultSize(1000, 1000)

        val intent = uCrop.getIntent(requireContext())
        cropResultLauncher.launch(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}