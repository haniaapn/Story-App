package com.dicoding.storyappdicoding.ui.add_story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dicoding.storyappdicoding.R
import com.dicoding.storyappdicoding.databinding.ActivityAddStoryBinding
import com.dicoding.storyappdicoding.reduceFileImage
import com.dicoding.storyappdicoding.rotateFile
import com.dicoding.storyappdicoding.ui.ViewModelFactory
import com.dicoding.storyappdicoding.ui.camera.CameraActivity
import com.dicoding.storyappdicoding.data.remote.Result
import com.dicoding.storyappdicoding.ui.home.MainActivity
import com.dicoding.storyappdicoding.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var addViewModel: AddViewModel

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var getFile: File? = null
    private var lat: Float? = null
    private var lon: Float? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_new_story)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        val factory: ViewModelFactory = ViewModelFactory.getModelFactory(this)
        val viewModel: AddViewModel by viewModels { factory }
        addViewModel = viewModel

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        binding.switchLocation.setOnCheckedChangeListener { _, isCheked ->
            if (isCheked) { getMyLocation() }
            else {
                lat = null
                lon = null
            }
        }
        binding.icCamera.setOnClickListener { startCameraX() }
        binding.icGaleri.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadStory() }
        observeAddStoryResult()
    }

    private fun getMyLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_CODE_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    lat = location.latitude.toFloat()
                    lon = location.longitude.toFloat()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to get location: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }



    private fun uploadStory() {
        val description = binding.edAddDescription.text.toString().trim()
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            addViewModel.addNewStory(description,imageMultipart , lat, lon)
            observeAddStoryResult()
        } else {
            Toast.makeText(this, "Please select an image and enter a description", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeAddStoryResult() {
        addViewModel.resultAddStory.observe(this) { result ->
            when (result) {
                is Result.Loading -> showLoading(true)
                is Result.Success -> {
                    showLoading(false)
                    handleAddStorySuccess()
                }
                is Result.Error -> {
                    showLoading(false)
                    handleAddStoryError(result.error)
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonAdd.isEnabled = !isLoading
    }

    private fun handleAddStoryError(errorMessage: String?) {
        Toast.makeText(this, "Failed to add story: $errorMessage", Toast.LENGTH_SHORT).show()
    }

    private fun handleAddStorySuccess() {
        Toast.makeText(this, "Story added successfully", Toast.LENGTH_SHORT).show()
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivItemPhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.ivItemPhoto.setImageURI(uri)
            }
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        const val REQUEST_CODE_PERMISSIONS = 10
        const val REQUEST_CODE_LOCATION_PERMISSION = 11

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

}