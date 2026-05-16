package pers.mekomimi.bookvault.db.book

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "book",
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