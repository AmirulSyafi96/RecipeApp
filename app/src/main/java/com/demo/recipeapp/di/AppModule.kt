package com.demo.recipeapp.di

import android.app.Application
import androidx.room.Room
import com.demo.recipeapp.data.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: RecipeDatabase.Callback
    ) = Room.databaseBuilder(app, RecipeDatabase::class.java, "recipe_database.db")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
      //  .allowMainThreadQueries()
        .build()

    @Provides
    fun provideRecipeDao(db: RecipeDatabase) = db.recipeDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope