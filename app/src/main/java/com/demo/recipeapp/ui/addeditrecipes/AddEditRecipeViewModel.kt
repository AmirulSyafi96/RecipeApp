package com.demo.recipeapp.ui.addeditrecipes

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.data.RecipeDao
import com.demo.recipeapp.ui.ADD_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.EDIT_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.recipes.RecipesViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditRecipeViewModel @ViewModelInject constructor(
    private val recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val recipe = state.get<Recipe>("recipe")

    var recipeName = state.get<String>("recipeName") ?: recipe?.name ?: ""
        set(value) {
            field = value
            state.set("recipeName", value)
        }

    var recipeDesc = state.get<String>("recipeDesc") ?: recipe?.description ?: ""
        set(value) {
            field = value
            state.set("recipeDesc", value)
        }


    var recipeType = state.get<String>("recipeType") ?: recipe?.type ?: ""
        set(value) {
            field = value
            state.set("recipeType", value)
        }

    var recipeFile = state.get<String>("recipeFile") ?: recipe?.file ?: ""
        set(value) {
            field = value
            state.set("recipeFile", value)
        }

    var recipePath = state.get<String>("recipePath") ?: recipe?.path ?: ""
        set(value) {
            field = value
            state.set("recipePath", value)
        }

    var tempPath = state.get<String>("tempPath") ?: recipe?.path ?: ""
        set(value) {
            field = value
            state.set("tempPath", value)
        }

    private val addEditRecipeEventChannel = Channel<AddEditRecipeEvent>()
    val addEditRecipeEvent = addEditRecipeEventChannel.receiveAsFlow()

    fun onPermissionResult(isGranted: Boolean) = viewModelScope.launch {
        if (isGranted) {
            addEditRecipeEventChannel.send(AddEditRecipeEvent.TakePicture)
        } else {
            addEditRecipeEventChannel.send(AddEditRecipeEvent.ShowRequirePermissionMessage)
        }
    }


    fun onSaveClick() {
        if (recipeName.isBlank()) {
            showInvalidInputMessage("Recipe name cannot be empty")
            return
        }
        if (recipeType.isBlank()) {
            showInvalidInputMessage("Please select recipe type")
            return
        }
        if (recipe != null) {
            if (tempPath.isNotEmpty()) {
                if (recipeFile.isEmpty()){
                    recipeFile = System.currentTimeMillis().toString() + ".jpg"
                }
                copyImage()
            }
            val updatedRecipe = recipe.copy(
                name = recipeName,
                description = recipeDesc,
                type = recipeType,
                file = recipeFile,
                path = recipePath
            )
            updateRecipe(updatedRecipe)
        } else {
            if (tempPath.isNotEmpty()) {
                recipeFile = System.currentTimeMillis().toString() + ".jpg"
                copyImage()
            }

            val newRecipe = Recipe(
                name = recipeName,
                description = recipeDesc,
                type = recipeType,
                file = recipeFile,
                path = recipePath
            )
            createRecipe(newRecipe)
        }


    }

    private fun createRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.insertRecipe(recipe)
        addEditRecipeEventChannel.send(AddEditRecipeEvent.NavigateBackWithResult(ADD_RECIPE_RESULT_OK,recipe))
    }

    private fun updateRecipe(recipe: Recipe) = viewModelScope.launch {
        recipeDao.updateRecipe(recipe)
        addEditRecipeEventChannel.send(AddEditRecipeEvent.NavigateBackWithResult(EDIT_RECIPE_RESULT_OK,recipe))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditRecipeEventChannel.send(AddEditRecipeEvent.ShowInvalidInputMessage(text))
    }

    private fun copyImage() = viewModelScope.launch {
        addEditRecipeEventChannel.send(AddEditRecipeEvent.CopyImage)
    }

    sealed class AddEditRecipeEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditRecipeEvent()
        data class NavigateBackWithResult(val result: Int,val recipe: Recipe) : AddEditRecipeEvent()
        object ShowRequirePermissionMessage : AddEditRecipeEvent()
        object TakePicture : AddEditRecipeEvent()
        object CopyImage : AddEditRecipeEvent()
    }
}