package pers.mekomimi.bookvault.db.folds

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FolderEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
}