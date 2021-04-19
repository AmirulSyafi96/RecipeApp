package com.demo.recipeapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    @Query("SELECT * FROM Recipe WHERE type =:type ORDER BY created DESC")
    fun getRecipesByType(type: String): Flow<List<Recipe>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: Recipe) : Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Delete
    suspend fun deleteRecipe(recipe: Recipe)

    //ingredients
    @Query("SELECT * FROM Ingredient WHERE recipeId =:recipeId ORDER BY created DESC")
    fun getIngredientsByRecipe(recipeId: Long): Flow<List<Ingredient>>

    @Query("SELECT * FROM Ingredient WHERE recipeId =:recipeId ORDER BY created DESC")
    suspend fun getIngredients(recipeId: Long): List<Ingredient>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient)

    @Update
    suspend fun updateIngredient(ingredient: Ingredient)

    @Delete
    suspend fun deleteIngredient(ingredient: Ingredient)

    //steps
    @Query("SELECT * FROM Step WHERE recipeId =:recipeId ORDER BY created DESC")
    fun getStepsByRecipe(recipeId: Long): Flow<List<Step>>

    @Query("SELECT * FROM Step WHERE recipeId =:recipeId ORDER BY created DESC")
    suspend fun getSteps(recipeId: Long): List<Step>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStep(step: Step)

    @Update
    suspend fun updateStep(step: Step)

    @Delete
    suspend fun deleteStep(step: Step)

  /*  @Query("DELETE FROM task_table WHERE completed = 1")
    suspend fun deleteCompletedTasks()*/
}