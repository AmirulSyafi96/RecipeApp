package com.demo.recipeapp.ui.addeditrecipes

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.demo.recipeapp.R
import com.demo.recipeapp.databinding.FragmentAddEditRecipeBinding
import com.demo.recipeapp.util.checkParentDir
import com.demo.recipeapp.util.compressImage
import com.demo.recipeapp.util.copyFile
import com.demo.recipeapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_add_edit_recipe.*
import kotlinx.coroutines.flow.collect
import java.io.File

@AndroidEntryPoint
class AddEditRecipeFragment : Fragment(R.layout.fragment_add_edit_recipe),
    AdapterView.OnItemClickListener {
    private val viewModel: AddEditRecipeViewModel by viewModels()

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            // Do something if permission granted
            if (isGranted) {
                Log.d("AddRecipeFragment", "permission granted")
                viewModel.onPermissionResult(isGranted)
            } else {
                Log.d("AddRecipeFragment", "permission denied")
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.CAMERA
                    )
                ) {
                    viewModel.onPermissionResult(isGranted)
                }
            }

        }
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data

                val file = File(requireActivity().filesDir, "pic.jpg")
                viewModel.tempPath = file.absolutePath

                compressImage(file)
                Glide.with(requireContext()).load(file).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(image_view_recipe)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditRecipeBinding.bind(view)

        val types = resources.getStringArray(R.array.recipe_type)

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.recipetypes, types)

        binding.apply {

            editTextType.apply {
                editTextType.setAdapter(spinnerAdapter)
                onItemClickListener = this@AddEditRecipeFragment
                if (viewModel.recipeType.isNotEmpty()) {
                    editTextType.setText(viewModel.recipeType, false)
                }
            }

            editTextRecipeName.setText(viewModel.recipeName)
            editTextRecipeDescription.setText(viewModel.recipeDesc)

            if (viewModel.recipePath.isNotEmpty()) {
                Glide.with(requireContext()).load(viewModel.recipePath).diskCacheStrategy(
                    DiskCacheStrategy.NONE
                ).skipMemoryCache(true).into(image_view_recipe)
            }

            editTextRecipeName.addTextChangedListener {
                viewModel.recipeName = it.toString()
            }

            editTextRecipeDescription.addTextChangedListener {
                viewModel.recipeDesc = it.toString()
            }

            fabSaveRecipe.setOnClickListener {
                viewModel.onSaveClick()
            }

            buttonAddImage.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission.launch(Manifest.permission.CAMERA)
                } else viewModel.onPermissionResult(true)
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditRecipeEvent.collect { event ->
                when (event) {
                    is AddEditRecipeViewModel.AddEditRecipeEvent.NavigateBackWithResult -> {
                        binding.editTextRecipeName.clearFocus()
                        binding.editTextRecipeDescription.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            bundleOf(
                                "add_edit_result" to event.result,
                                "recipe" to event.recipe
                            )
                        )
                        findNavController().popBackStack()
                    }
                    is AddEditRecipeViewModel.AddEditRecipeEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditRecipeViewModel.AddEditRecipeEvent.ShowRequirePermissionMessage -> {
                        val builder = AlertDialog.Builder(requireActivity())
                        builder.setTitle("Permission Required")
                        builder.setCancelable(false)
                        builder.setMessage("Camera permission is required in order to use this feature.")
                        builder.setPositiveButton(
                            "Setting"
                        ) { dialog: DialogInterface?, id: Int ->
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                requireActivity().packageName,
                                null
                            )
                            intent.data = uri
                            startActivity(intent)
                        }
                        builder.setNegativeButton("Close", null)
                        val dialog = builder.create()
                        dialog.show()
                    }
                    is AddEditRecipeViewModel.AddEditRecipeEvent.TakePicture -> {
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        val photoFile = File(requireActivity().filesDir, "pic.jpg")
                        val photoURI = FileProvider.getUriForFile(
                            requireContext(),
                            requireActivity().applicationContext.packageName + ".provider",
                            photoFile
                        )
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        resultLauncher.launch(intent)
                    }
                    is AddEditRecipeViewModel.AddEditRecipeEvent.CopyImage -> {

                        val parent: File = checkParentDir(requireActivity(), "RecipeImages")!!

                        val targetLocation = File(parent, viewModel.recipeFile)

                        viewModel.recipePath = targetLocation.absolutePath

                        val file = File(requireActivity().filesDir, "pic.jpg")

                        copyFile(targetLocation, file);

                    }

                }.exhaustive

            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type: String = edit_text_type.text.toString()
        viewModel.recipeType = type
    }

}