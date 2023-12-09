package com.boostcampwm2023.snappoint.presentation.createpost

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.navArgs
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.ActivityCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseActivity
import com.boostcampwm2023.snappoint.presentation.markerpointselector.MarkerPointSelectorActivity
import com.boostcampwm2023.snappoint.presentation.model.PositionState
import com.boostcampwm2023.snappoint.presentation.util.MetadataUtil
import com.boostcampwm2023.snappoint.presentation.util.getBitmapFromUri
import com.boostcampwm2023.snappoint.presentation.util.resizeBitmap
import com.boostcampwm2023.snappoint.presentation.util.untilSixAfterDecimalPoint
import com.boostcampwm2023.snappoint.presentation.videoedit.VideoEditActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostActivity : BaseActivity<ActivityCreatePostBinding>(R.layout.activity_create_post) {

    private val viewModel: CreatePostViewModel by viewModels()
    private val args: CreatePostActivityArgs by navArgs()

    private val imagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.containsValue(false).not()) {
                launchImageSelectionLauncher()
            } else {
                showToastMessage(R.string.message_image_permission_denied)
            }
        }

    private val videoPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.containsValue(false).not()) {
                launchVideoSelectionLauncher()
            } else {
                showToastMessage(R.string.message_video_permission_denied)
            }
        }

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data ?: return@registerForActivityResult
                val inputStream = this.contentResolver.openInputStream(imageUri)
                    ?: return@registerForActivityResult
                val position = MetadataUtil.extractLocationFromInputStream(inputStream)
                    .getOrDefault(PositionState(0.0, 0.0))
                val bitmap = resizeBitmap(getBitmapFromUri(this, imageUri), 1280)
                viewModel.addImageBlock(bitmap, position)

                startMapActivityAndFindAddress(viewModel.uiState.value.postBlocks.lastIndex, position)
            }
        }

    private val videoSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val videoUri = result.data?.data ?: return@registerForActivityResult
                Log.d("TAG", "videoUri: $videoUri")
               val inputStream = this.contentResolver.openInputStream(videoUri)
                    ?: return@registerForActivityResult
                val position = MetadataUtil.extractLocationFromInputStream(inputStream)
                    .getOrDefault(PositionState(0.0, 0.0))

                //val bitmap = resizeBitmap(getBitmapFromUri(this, imageUri), 1280)
                viewModel.addVideoBlock(videoUri, position)

                //startMapActivityAndFindAddress(viewModel.uiState.value.postBlocks.lastIndex, position)
                startVideoEditActivity(viewModel.uiState.value.postBlocks.lastIndex, videoUri)
            }
        }



    private val addressSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    viewModel.setAddressAndPosition(
                        index = it.getIntExtra("index", 0),
                        address = it.getStringExtra("address") ?: "",
                        position = PositionState(
                            it.getDoubleExtra("latitude", 0.0).untilSixAfterDecimalPoint(),
                            it.getDoubleExtra("longitude", 0.0).untilSixAfterDecimalPoint()
                        )
                    )
                }
            }
        }

    private val videoEditLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let{
                    /*val outputUri = it.getStringExtra("output") ?: return@registerForActivityResult
                    val file = this.contentResolver.openInputStream(outputUri.toUri())
                    viewModel.setVideo(
                        index = it.getIntExtra("index", 0),
                        edittedVideo = it.getBy
                    )*/
                }
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        collectViewModelData()
    }

    private fun initBinding() {
        with(binding) {
            vm = viewModel
            btnCheck.setOnClickListener {
                viewModel.onCheckButtonClicked()
            }
        }
    }

    private fun collectViewModelData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                launch {
                    viewModel.event.collect { event ->
                        when (event) {
                            is CreatePostEvent.ShowMessage -> {
                                showToastMessage(event.resId)
                            }

                            is CreatePostEvent.SelectImageFromLocal -> {
                                selectImage()
                            }

                            is CreatePostEvent.NavigatePrev -> {
                                finish()
                            }

                            is CreatePostEvent.FindAddress -> {
                                startMapActivityAndFindAddress(event.index, event.position)
                            }

                            CreatePostEvent.SelectVideoFromLocal -> {
                                selectVideo()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun selectVideo() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )

            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        getVideoWithPermissionCheck(permissions)
    }

    private fun selectImage() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION
            )

            else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        getImageWithPermissionCheck(permissions)
    }

    private fun launchImageSelectionLauncher() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            "image/*"
        )
        imageSelectionLauncher.launch(intent)
    }

    private fun launchVideoSelectionLauncher() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            "video/*"
        )
        videoSelectionLauncher.launch(intent)
    }

    private fun getImageWithPermissionCheck(permissions: Array<String>) {
        val permissionCheck = permissions.filter {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_DENIED
        }

        if (permissionCheck.isNotEmpty()) {
            if (permissionCheck.any { shouldShowRequestPermissionRationale(it) }) {
                showToastMessage(R.string.message_image_permission_required)
            }
        }
        imagePermissionLauncher.launch(permissions)
    }

    private fun getVideoWithPermissionCheck(permissions: Array<String>) {
        val permissionCheck = permissions.filter {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_DENIED
        }

        if (permissionCheck.isNotEmpty()) {
            if (permissionCheck.any { shouldShowRequestPermissionRationale(it) }) {
                showToastMessage(R.string.message_video_permission_required)
            }
        }
        videoPermissionLauncher.launch(permissions)
    }

    private fun startMapActivityAndFindAddress(index: Int, position: PositionState) {
        val intent = Intent(this, MarkerPointSelectorActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("position", position.asDoubleArray())
        addressSelectionLauncher.launch(intent)
    }

    private fun startVideoEditActivity(index: Int, uri: Uri) {
        val intent = Intent(this, VideoEditActivity::class.java)
        intent.putExtra("index", index)
        intent.putExtra("uri", uri.toString())
        videoEditLauncher.launch(intent)
    }
}