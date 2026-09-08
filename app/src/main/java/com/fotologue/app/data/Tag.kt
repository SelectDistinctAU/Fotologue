package com.fotologue.app.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "tags",
    foreignKeys = [
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentId")]
)
@Serializable
data class Tag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String = CATEGORY_CONTENT,
    val parentId: Long? = null,
    val position: Int = 0
) {
    companion object {
        const val CATEGORY_SUBJECT = "Subject"
        const val CATEGORY_CONTENT = "Content"

        val ALL_CATEGORIES = listOf(
            CATEGORY_SUBJECT,
            CATEGORY_CONTENT
        )
    }
}

@Entity(
    tableName = "log_entry_tag_cross_ref",
    primaryKeys = ["logEntryId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = LogEntry::class,
            parentColumns = ["id"],
            childColumns = ["logEntryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("logEntryId"),
        Index("tagId")
    ]
)
@Serializable
data class LogEntryTagCrossRef(
    val logEntryId: Long,
    val tagId: Long
)
