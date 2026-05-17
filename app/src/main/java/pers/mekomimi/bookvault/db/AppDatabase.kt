package pers.mekomimi.bookvault.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pers.mekomimi.bookvault.db.book.Book
import pers.mekomimi.bookvault.db.book.BookDao
import pers.mekomimi.bookvault.db.folder.FolderDao
import pers.mekomimi.bookvault.db.folder.Folder

@Database(
    entities = [
        Book::class,
        Folder::class
               ],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun folderDao(): FolderDao
}