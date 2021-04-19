package com.demo.recipeapp.ui.recipes

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.demo.recipeapp.data.*
import com.demo.recipeapp.ui.ADD_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.EDIT_RECIPE_RESULT_OK
import com.demo.recipeapp.util.deleteRecursive
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File

class RecipesViewModel @ViewModelInject constructor(
    private val recipeDao: RecipeDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    //val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val recipesEventChannel = Channel<RecipesEvent>()
    val recipesEvent = recipesEventChannel.receiveAsFlow()

    private val recipesFlow = combine(
        preferencesFlow
    ) { filterPreferences ->
        filterPreferences
    }.flatMapLatest { (filterPreferences) ->
        recipeDao.getRecipesByType(filterPreferences.type)
    }

    val recipes = recipesFlow.asLiveData()

    fun onTypeSelected(type: String) = viewModelScope.launch {
        preferencesManager.updateTypeSelection(type)
    }

    fun onRecipeSwiped(recipe: Recipe) = viewModelScope.launch {
        val ingredients: List<Ingredient> = recipeDao.getIngredients(recipe.recipeId)
        val steps: List<Step> = recipeDao.getSteps(recipe.recipeId)
        recipeDao.deleteRecipe(recipe)

        if (recipe.file.isNotEmpty()){
            //delete image
            deleteRecursive(
                File(
                    recipe.path
                )
            )
        }

        recipesEventChannel.send(
            RecipesEvent.ShowUndoDeleteRecipeMessage(
                recipe,
                ingredients,
                steps
            )
        )
    }

    fun onUndoDeleteClick(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        steps: List<Step>
    ) = viewModelScope.launch {
        recipeDao.insertRecipe(recipe)
        for (item in ingredients) {
            recipeDao.insertIngredient(item)
        }
        for (item in steps) {
            recipeDao.insertStep(item)
        }
    }

    fun onAddNewRecipeClick() = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.NavigateToAddRecipeScreen)
    }

    fun onRecipeSelected(recipe: Recipe) = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.NavigateToRecipeDetailsScreen(recipe))
    }

    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_RECIPE_RESULT_OK -> showTaskSavedConfirmationMessage("Recipe added")
            EDIT_RECIPE_RESULT_OK -> showTaskSavedConfirmationMessage("Recipe updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        recipesEventChannel.send(RecipesEvent.ShowTaskSavedConfirmationMessage(text))
    }

    sealed class RecipesEvent {
        object NavigateToAddRecipeScreen : RecipesEvent()
        data class NavigateToRecipeDetailsScreen(val recipe: Recipe) : RecipesEvent()
        data class ShowUndoDeleteRecipeMessage(
            val recipe: Recipe,
            val ingredients: List<Ingredient>,
            val steps: List<Step>
        ) : RecipesEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : RecipesEvent()
    }
}