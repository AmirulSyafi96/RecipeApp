package com.demo.recipeapp.ui.recipes

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.demo.recipeapp.R
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.databinding.FragmentRecipesBinding
import com.demo.recipeapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes.*
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment(R.layout.fragment_recipes), RecipesAdapter.OnItemClickListener,
    AdapterView.OnItemSelectedListener {

    private val viewModel: RecipesViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRecipesBinding.bind(view)

        val types = resources.getStringArray(R.array.recipe_type)

        val spinnerAdapter = ArrayAdapter(requireContext(), R.layout.recipetypes, types)

        val recipeAdapter = RecipesAdapter(this)

        binding.apply {

            spinnerType.apply {
                adapter = spinnerAdapter
                onItemSelectedListener = this@RecipesFragment
            }

            recyclerViewRecipes.apply {
                adapter = recipeAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        LinearLayoutManager.VERTICAL
                    )
                )
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val recipe = recipeAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onRecipeSwiped(recipe)
                }
            }).attachToRecyclerView(recyclerViewRecipes)


            fabAddRecipe.setOnClickListener {
                viewModel.onAddNewRecipeClick()
            }
        }

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            viewModel.onAddEditResult(result)
        }

        viewModel.recipes.observe(viewLifecycleOwner) {
            recipeAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipesEvent.collect { event ->
                when (event) {
                    is RecipesViewModel.RecipesEvent.ShowUndoDeleteRecipeMessage -> {
                        Snackbar.make(requireView(), "Recipe deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(
                                    event.recipe,
                                    event.ingredients,
                                    event.steps
                                )
                            }.show()
                    }
                    is RecipesViewModel.RecipesEvent.NavigateToAddRecipeScreen -> {
                        val action =
                            RecipesFragmentDirections.actionRecipesFragmentToAddRecipeFragment(
                                null,
                                "New Recipe"
                            )
                        findNavController().navigate(action)
                    }
                    is RecipesViewModel.RecipesEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is RecipesViewModel.RecipesEvent.NavigateToRecipeDetailsScreen -> {
                        val action =
                            RecipesFragmentDirections.actionRecipesFragmentToRecipeDetailsFragment(
                                event.recipe
                            )
                        findNavController().navigate(action)
                    }
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            val compareValue = viewModel.preferencesFlow.first().type
            val spinnerPosition: Int = spinnerAdapter.getPosition(compareValue)
            spinner_type.setSelection(spinnerPosition)
        }


    }

    override fun onItemClick(recipe: Recipe) {
        viewModel.onRecipeSelected(recipe)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val type: String = spinner_type.selectedItem.toString()
        //Toast.makeText(activity, "position  $type", Toast.LENGTH_SHORT).show()
        viewModel.onTypeSelected(type)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }
}