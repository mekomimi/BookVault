package pers.mekomimi.bookvault.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAll(): Flow<List<Book>>

    @Query("DELETE FROM books")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM books")
    suspend fun count(): Int
}