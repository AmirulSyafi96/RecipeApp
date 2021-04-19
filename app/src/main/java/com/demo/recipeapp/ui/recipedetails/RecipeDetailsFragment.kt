package com.demo.recipeapp.ui.recipedetails

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.demo.recipeapp.R
import com.demo.recipeapp.data.Ingredient
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.data.Step
import com.demo.recipeapp.databinding.FragmentRecipesDetailsBinding
import com.demo.recipeapp.ui.recipes.RecipesFragmentDirections
import com.demo.recipeapp.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_recipes_details.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class RecipeDetailsFragment : Fragment(R.layout.fragment_recipes_details),
    IngredientAdapter.OnItemClickListener, StepAdapter.OnItemClickListener {
    private val viewModel: RecipeDetailsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRecipesDetailsBinding.bind(view)

        setFragmentResultListener("add_edit_request") { _, bundle ->
            val result = bundle.getInt("add_edit_result")
            val type = bundle.getString("type", "")

            viewModel.onAddEditResult(result, type)

            val recipe: Recipe? = bundle.getParcelable("recipe")
            if (recipe != null) {
                viewModel.recipe = recipe
                text_recipe_name.text = viewModel.recipe?.name
                text_type.text = viewModel.recipe?.type
                text_description.text = viewModel.recipe?.description

                Glide.with(requireContext()).load(viewModel.recipe?.path).diskCacheStrategy(
                    DiskCacheStrategy.NONE
                )
                    .skipMemoryCache(true).placeholder(R.drawable.ic_image)
                    .into(image_view_recipe_details)

            }

        }

        val ingredientAdapter = IngredientAdapter(this)

        val stepAdapter = StepAdapter(this)

        binding.apply {
            textRecipeName.text = viewModel.recipe?.name
            textType.text = viewModel.recipe?.type
            textDescription.text = viewModel.recipe?.description

            Glide.with(requireContext()).load(viewModel.recipe?.path).diskCacheStrategy(
                DiskCacheStrategy.NONE
            )
                .skipMemoryCache(true).placeholder(R.drawable.ic_image)
                .into(image_view_recipe_details)

            recyclerViewIngredients.apply {
                adapter = ingredientAdapter
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
                        val ingredient = ingredientAdapter.currentList[viewHolder.adapterPosition]
                        viewModel.onIngredientSwiped(ingredient)
                    }
                }).attachToRecyclerView(recyclerViewIngredients)
            }

            recyclerViewSteps.apply {
                adapter = stepAdapter

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
                        val step = stepAdapter.currentList[viewHolder.adapterPosition]
                        viewModel.onStepSwiped(step)
                    }
                }).attachToRecyclerView(recyclerViewSteps)
            }

            buttonAddIngredients.setOnClickListener {
                viewModel.onIngredientAddClick()
            }

            buttonAddSteps.setOnClickListener {
                viewModel.onStepAddClick()
            }

        }

        viewModel.ingredients.observe(viewLifecycleOwner) {
            ingredientAdapter.submitList(it)
        }

        viewModel.steps.observe(viewLifecycleOwner) {
            stepAdapter.submitList(it)
        }

        button_edit_recipe.setOnClickListener {
            viewModel.onEditRecipe()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.recipeDetailsEvent.collect { event ->
                when (event) {
                    is RecipeDetailsViewModel.RecipeDetailsEvent.NavigateToEditRecipe -> {
                        val action =
                            RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToAddEditRecipeFragment(
                                event.recipe,
                                "Edit Recipe"
                            )
                        findNavController().navigate(action)
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.ShowRecipeSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.ShowUndoDeleteIngredientMessage -> {
                        Snackbar.make(requireView(), "Ingredient deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClickIngredient(event.ingredient)
                            }.show()
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.ShowUndoDeleteStepMessage -> {
                        Snackbar.make(requireView(), "Step deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClickStep(event.step)
                            }.show()
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.NavigateToIngredientDetail -> {
                        val action =
                            RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToAddEditDetailFragment(
                                "Ingredient",
                                event.ingredient, null, "Edit Ingredient", event.recipeId
                            )
                        findNavController().navigate(action)
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.NavigateToStepDetail -> {
                        val action =
                            RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToAddEditDetailFragment(
                                "Step",
                                null, event.step, "Edit Step", event.recipeId
                            )
                        findNavController().navigate(action)
                    }
                    is RecipeDetailsViewModel.RecipeDetailsEvent.NavigateToAddIngredientDetail -> {
                        val action =
                            RecipeDetailsFragmentDirections.actionRecipeDetailsFragmentToAddEditDetailFragment(
                                event.type,
                                null, null, "Add " + event.type, event.recipeId
                            )
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }
    }

    override fun onItemClick(ingredient: Ingredient) {
        viewModel.onIngredientClick(ingredient)
    }

    override fun onItemClick(step: Step) {
        viewModel.onStepClick(step)
    }
}