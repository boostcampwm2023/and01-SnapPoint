package com.boostcampwm2023.snappoint.presentation.createpost

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcampwm2023.snappoint.R
import com.boostcampwm2023.snappoint.databinding.FragmentCreatePostBinding
import com.boostcampwm2023.snappoint.presentation.base.BaseFragment
import com.boostcampwm2023.snappoint.presentation.util.MetadataUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreatePostFragment : BaseFragment<FragmentCreatePostBinding>(R.layout.fragment_create_post) {

    private val viewModel: CreatePostViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        collectViewModelData()
    }

    fun initBinding() {
        with(binding) {
            vm = viewModel
        }
    }

    private fun collectViewModelData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.event.collect{event ->
                    when(event){
                        is CreatePostEvent.ShowMessage -> {showToastMessage(event.resId)}

                        is CreatePostEvent.SelectImageFromLocal -> { selectImage() }
                    }
                }
            }

        }
    }

    private fun showToastMessage(resId: Int) {
        Toast.makeText(requireContext(), getString(resId), Toast.LENGTH_LONG).show()
    }

    private fun selectImage() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                imagePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    )
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                imagePermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_MEDIA_LOCATION
                    )
                )
            }

            else -> {
                imagePermissionLauncher.launch(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                )
            }
        }
    }

    private val imagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.containsValue(false).not()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.setDataAndType(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    "image/*"
                )
                imageSelectionLauncher.launch(intent)
            } else {
                Log.d("galleryPermissionLauncher", "permission denied.")
            }
        }

    private val imageSelectionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageUri = result.data?.data ?: return@registerForActivityResult
                val inputStream = requireContext().contentResolver.openInputStream(imageUri) ?: return@registerForActivityResult
                val position = MetadataUtil.extractLocationFromInputStream(inputStream).getOrDefault(Position(0.0, 0.0))
                viewModel.addImageBlock(imageUri, position)
            }
        }
}