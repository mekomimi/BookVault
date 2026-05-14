package pers.mekomimi.bookvault.db.books

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val path: String,
    val title: String,
    val author: String? = null,
    val ext: String,
    val mtime: Long
)