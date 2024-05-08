package org.d3if0065.assessment.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "article")
data class Article (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val category: String
)