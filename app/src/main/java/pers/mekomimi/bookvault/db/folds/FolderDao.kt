package pers.mekomimi.bookvault.db.folds

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FolderDao {

    @Insert
    suspend fun insert(folder: FolderEntity)

    @Query("SELECT * FROM FolderEntity")
    suspend fun getAll(): List<FolderEntity>

    @Delete
    suspend fun delete(folder: FolderEntity)
}