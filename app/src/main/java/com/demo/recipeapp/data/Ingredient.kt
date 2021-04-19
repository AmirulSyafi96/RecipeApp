package com.demo.recipeapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(foreignKeys = [
        ForeignKey(entity = Recipe::class,
            parentColumns = ["recipeId"],
            childColumns = ["recipeId"],
            onDelete = CASCADE)])
@Parcelize
data class Ingredient(
    @ColumnInfo(index = true)
    val recipeId: Long,
    val ingredient: String,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val ingredientId: Long = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}