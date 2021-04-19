package com.demo.recipeapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity
@Parcelize
data class Recipe(
    var name: String,
    val description: String,
    val type: String,
    val file: String,
    val path: String,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val recipeId: Long = 0
) : Parcelable{
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}