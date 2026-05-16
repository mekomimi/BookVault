package pers.mekomimi.bookvault.db.folder

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FolderDao {

    @Insert
    suspend fun insert(folder: Folder)

    @Query("SELECT * FROM Folder")
    suspend fun getAll(): List<Folder>

    @Query("SELECT * FROM Folder WHERE uri = :uri LIMIT 1")
    suspend fun findByUri(uri: String):Folder?

    @Delete
    suspend fun delete(folder: Folder)
}