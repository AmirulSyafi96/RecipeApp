package com.demo.recipeapp.ui.recipedetails

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.demo.recipeapp.data.Ingredient
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.data.RecipeDao
import com.demo.recipeapp.data.Step
import com.demo.recipeapp.ui.ADD_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.EDIT_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.addeditrecipes.AddEditRecipeViewModel
import com.demo.recipeapp.ui.recipes.RecipesViewModel
import com.demo.recipeapp.util.deleteRecursive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

class RecipeDetailsViewModel @ViewModelInject constructor(
    private val recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    var recipe = state.get<Recipe>("recipe")

    val recipeId = state.getLiveData("recipeId", recipe?.recipeId)


    private val ingredientsFlow = combine(
        recipeId.asFlow()
    ) { filterPreferences ->
        filterPreferences
    }.flatMapLatest { _ ->
        recipeDao.getIngredientsByRecipe(recipeId = recipe?.recipeId!!)
    }

    val ingredients = ingredientsFlow.asLiveData()

    private val stepsFlow = combine(
        recipeId.asFlow()
    ) { filterPreferences ->
        filterPreferences
    }.flatMapLatest { _ ->
        recipeDao.getStepsByRecipe(recipeId = recipe?.recipeId!!)
    }

    val steps = stepsFlow.asLiveData()

    private val recipeDetailsEventChannel = Channel<RecipeDetailsEvent>()
    val recipeDetailsEvent = recipeDetailsEventChannel.receiveAsFlow()

    fun onEditRecipe() = viewModelScope.launch {
        recipeDetailsEventChannel.send(RecipeDetailsEvent.NavigateToEditRecipe(recipe))
    }

    fun onAddEditResult(result: Int, type: String) {
        if (type.isNotEmpty()) {
            when (result) {
                ADD_RECIPE_RESULT_OK -> showRecipeSavedConfirmationMessage("$type added")
                EDIT_RECIPE_RESULT_OK -> showRecipeSavedConfirmationMessage("$type updated")
            }
        } else {
            when (result) {
                ADD_RECIPE_RESULT_OK -> showRecipeSavedConfirmationMessage("Recipe added")
                EDIT_RECIPE_RESULT_OK -> showRecipeSavedConfirmationMessage("Recipe updated")
            }
        }

    }

    private fun showRecipeSavedConfirmationMessage(text: String) = viewModelScope.launch {
        recipeDetailsEventChannel.send(RecipeDetailsEvent.ShowRecipeSavedConfirmationMessage(text))
    }

    fun onIngredientSwiped(ingredient: Ingredient) = viewModelScope.launch {
        recipeDao.deleteIngredient(ingredient)
        recipeDetailsEventChannel.send(RecipeDetailsEvent.ShowUndoDeleteIngredientMessage(ingredient))
    }

    fun onStepSwiped(step: Step) = viewModelScope.launch {
        recipeDao.deleteStep(step)
        recipeDetailsEventChannel.send(RecipeDetailsEvent.ShowUndoDeleteStepMessage(step))
    }

    fun onUndoDeleteClickIngredient(ingredient: Ingredient) = viewModelScope.launch {
        recipeDao.insertIngredient(ingredient)

    }

    fun onUndoDeleteClickStep(step: Step) = viewModelScope.launch {
        recipeDao.insertStep(step)
    }

    fun onIngredientClick(ingredient: Ingredient) = viewModelScope.launch {
        recipeDetailsEventChannel.send(
            RecipeDetailsEvent.NavigateToIngredientDetail(
                ingredient,
                recipe!!.recipeId
            )
        )
    }

    fun onStepClick(step: Step) = viewModelScope.launch {
        recipeDetailsEventChannel.send(
            RecipeDetailsEvent.NavigateToStepDetail(
                step,
                recipe!!.recipeId
            )
        )
    }

    fun onIngredientAddClick() = viewModelScope.launch {
        recipeDetailsEventChannel.send(
            RecipeDetailsEvent.NavigateToAddIngredientDetail(
                "Ingredient",
                recipe!!.recipeId
            )
        )
    }

    fun onStepAddClick() = viewModelScope.launch {
        recipeDetailsEventChannel.send(
            RecipeDetailsEvent.NavigateToAddIngredientDetail(
                "Step",
                recipe!!.recipeId
            )
        )
    }

    sealed class RecipeDetailsEvent {
        data class NavigateToEditRecipe(val recipe: Recipe?) : RecipeDetailsEvent()
        data class ShowRecipeSavedConfirmationMessage(val msg: String) : RecipeDetailsEvent()
        data class ShowUndoDeleteIngredientMessage(val ingredient: Ingredient) :
            RecipeDetailsEvent()

        data class ShowUndoDeleteStepMessage(val step: Step) :
            RecipeDetailsEvent()

        data class NavigateToIngredientDetail(val ingredient: Ingredient, val recipeId: Long) :
            RecipeDetailsEvent()

        data class NavigateToAddIngredientDetail(val type: String, val recipeId: Long) :
            RecipeDetailsEvent()

        data class NavigateToStepDetail(val step: Step, val recipeId: Long) : RecipeDetailsEvent()
    }
}