package pers.mekomimi.bookvault.db.book

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: Book)

    @Query("SELECT * FROM book ORDER BY title ASC")
    fun getAll(): Flow<List<Book>>

    @Query("DELETE FROM book")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM book")
    suspend fun count(): Int

    @Query("SELECT * FROM book WHERE uri = :uri LIMIT 1")
    suspend fun findByUri(uri: String): Book?

    @Delete
    suspend fun delete(book: Book)
}