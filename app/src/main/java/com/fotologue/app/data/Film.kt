package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "films")
@Serializable
data class Film(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val iso: Int,
    val type: String,
    val expectedExposures: Int = 36,
    val format: String = "35mm"
) {
    companion object {
        val FORMAT_35MM = "35mm"
        val FORMAT_120 = "120 (Medium Format)"
        val FORMAT_LARGE = "Large Format / Sheet"

        val ALL_FORMATS = listOf(FORMAT_35MM, FORMAT_120, FORMAT_LARGE)
        val TYPE_COLOR_NEGATIVE = "Colour Negative (C-41)"
        val TYPE_BLACK_AND_WHITE = "Black and White (B&W)"
        val TYPE_SLIDE = "Slide/Reversal (E-6)"

        val ALL_TYPES = listOf(
            TYPE_COLOR_NEGATIVE,
            TYPE_BLACK_AND_WHITE,
            TYPE_SLIDE
        )
    }
}
