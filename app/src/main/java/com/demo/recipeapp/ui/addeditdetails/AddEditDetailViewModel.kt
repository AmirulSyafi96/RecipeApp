package com.demo.recipeapp.ui.addeditdetails

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.demo.recipeapp.data.Ingredient
import com.demo.recipeapp.data.Recipe
import com.demo.recipeapp.data.RecipeDao
import com.demo.recipeapp.data.Step
import com.demo.recipeapp.ui.ADD_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.EDIT_RECIPE_RESULT_OK
import com.demo.recipeapp.ui.addeditrecipes.AddEditRecipeViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditDetailViewModel @ViewModelInject constructor(
    private val recipeDao: RecipeDao,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    var ingredient = state.get<Ingredient>("ingredient")

    val step = state.get<Step>("step")

    val type = state.get<String>("type")

    val recipeId = state.get<Long>("recipeId")

    var detail = state.get<String>("detail") ?: ingredient?.ingredient ?: step?.step ?:""
        set(value) {
            field = value
            state.set("detail", value)
        }


    private val addEditDetailEventChannel = Channel<AddEditDetailEvent>()
    val addEditDetailEvent = addEditDetailEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (detail.isBlank()) {
            showInvalidInputMessage("$type value cannot be empty")
            return
        }

        if (type?.equals("Ingredient") == true) {
            if (ingredient != null){
                val updateIngredient = ingredient!!.copy(
                    ingredient = detail
                )
                updateIngredient(updateIngredient)
            }else{
                val newIngredient = Ingredient(
                    ingredient = detail,
                    recipeId = recipeId!!,
                )
                createIngredient(newIngredient)
            }

        } else {
            if (step != null){
                val updateStep = step!!.copy(
                    step = detail
                )
                updateStep(updateStep)
            }else{
                val newStep = Step(
                    step = detail,
                    recipeId = recipeId!!,
                )
                createStep(newStep)
            }

        }
    }

    private fun createIngredient(ingredient: Ingredient) = viewModelScope.launch {
        recipeDao.insertIngredient(ingredient)
        addEditDetailEventChannel.send(
            AddEditDetailEvent.NavigateBackWithResult(
                ADD_RECIPE_RESULT_OK,
                type!!
            )
        )
    }

    private fun updateIngredient(ingredient: Ingredient) = viewModelScope.launch {
        recipeDao.updateIngredient(ingredient)
        addEditDetailEventChannel.send(
            AddEditDetailEvent.NavigateBackWithResult(
                EDIT_RECIPE_RESULT_OK,
                type!!
            )
        )
    }

    private fun createStep(step: Step) = viewModelScope.launch {
        recipeDao.insertStep(step)
        addEditDetailEventChannel.send(
            AddEditDetailEvent.NavigateBackWithResult(
                ADD_RECIPE_RESULT_OK,
                type!!
            )
        )
    }

    private fun updateStep(step: Step) = viewModelScope.launch {
        recipeDao.updateStep(step)
        addEditDetailEventChannel.send(
            AddEditDetailEvent.NavigateBackWithResult(
                EDIT_RECIPE_RESULT_OK,
                type!!
            )
        )
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditDetailEventChannel.send(AddEditDetailEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditDetailEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditDetailEvent()
        data class NavigateBackWithResult(val result: Int, val type: String) : AddEditDetailEvent()
    }
}