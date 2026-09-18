package com.fotologue.core.data.lab

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "print_outcomes",
    foreignKeys = [
        ForeignKey(
            entity = PrintRecipe::class,
            parentColumns = ["id"],
            childColumns = ["printRecipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("printRecipeId")]
)
@Serializable
data class PrintOutcome(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val printRecipeId: Long,
    val editionNumber: String,
    val status: String, // ARCHIVED, DISPLAY_EXHIBITION, SOLD, DISPOSED
    val exhibitionName: String? = null,
    val buyerName: String? = null,
    val salePrice: String? = null,
    val saleDate: Long? = null,
    val notes: String = ""
)
