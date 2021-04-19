package com.demo.recipeapp.ui.addeditdetails


import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.demo.recipeapp.R
import com.demo.recipeapp.databinding.FragmentAddEditDetailBinding
import com.demo.recipeapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class AddEditDetailFragment : Fragment(R.layout.fragment_add_edit_detail) {
    private val viewModel: AddEditDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAddEditDetailBinding.bind(view)

        binding.apply {
            textInputLayoutDetail.setHint(viewModel.type.toString())
            editTextDetailName.setText(viewModel.detail)
            editTextDetailName.addTextChangedListener {
                viewModel.detail = it.toString()
            }
            fabSaveDetail.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditDetailEvent.collect { event ->
               when(event){
                   is AddEditDetailViewModel.AddEditDetailEvent.NavigateBackWithResult -> {
                       binding.editTextDetailName.clearFocus()
                       setFragmentResult(
                           "add_edit_request",
                           bundleOf(
                               "add_edit_result" to event.result,
                               "type" to event.type
                           )
                       )
                       findNavController().popBackStack()
                   }
                   is AddEditDetailViewModel.AddEditDetailEvent.ShowInvalidInputMessage -> {
                       Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                   }
               }.exhaustive

            }
        }

    }
}