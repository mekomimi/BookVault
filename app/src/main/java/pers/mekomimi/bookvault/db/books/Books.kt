package pers.mekomimi.bookvault.db.books

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "books",
    indices = [Index(value = ["uri"], unique = true)]
)
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val title: String,
    val author: String? = null,
    val ext: String,
    val mtime: Long
)