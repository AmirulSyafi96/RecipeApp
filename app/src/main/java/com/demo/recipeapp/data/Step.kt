package com.demo.recipeapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat

@Entity(foreignKeys = [
    ForeignKey(entity = Recipe::class,
        parentColumns = ["recipeId"],
        childColumns = ["recipeId"],
        onDelete = ForeignKey.CASCADE
    )])
@Parcelize
data class Step(
    @ColumnInfo(index = true)
    val recipeId: Long,
    val step: String,
    val created: Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true)
    val stepId: Long = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(created)
}