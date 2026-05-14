package pers.mekomimi.bookvault.db.folds

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FolderEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val uri: String
)