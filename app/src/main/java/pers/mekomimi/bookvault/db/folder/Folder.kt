package pers.mekomimi.bookvault.db.folder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Folder(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val uri: String
)