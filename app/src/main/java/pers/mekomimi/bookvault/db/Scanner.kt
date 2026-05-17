package pers.mekomimi.bookvault.db

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import pers.mekomimi.bookvault.db.book.Book
import pers.mekomimi.bookvault.db.book.BookDao
import pers.mekomimi.bookvault.db.folder.Folder

class Scanner(
    private val context: Context,
    private val db: AppDatabase
) {

    suspend fun selectFolder(uri: Uri) {

        context.contentResolver
            .takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

        db.folderDao().insert(
            Folder(
                uri = uri.toString()
            )
        )
    }

    suspend fun scanAllFolders() {
        val oldBooks =db.bookDao().getAll().associateBy { it.uri }

        val folders = db.folderDao().getAll()

        for (folder in folders) {

            val root = DocumentFile.fromTreeUri(
                context,
                folder.uri.toUri()
            ) ?: continue

            scanBooks(root, db.bookDao(), oldBooks=oldBooks)
        }
    }

    private suspend fun scanBooks(
        dir: DocumentFile,
        dao: BookDao,
        oldBooks: Map<String, Book>
    ) {
        val files = dir.listFiles()

        for (file in files) {
            // 递归目录
            if (file.isDirectory) {

                scanBooks(file, dao, oldBooks)
            }
            // 处理文件
            else if (file.isFile && isBook(file)) {

                val book = Book(

                    // SAF 没有 absolutePath
                    uri = file.uri.toString(),

                    title = file.name
                        ?.substringBeforeLast(".")
                        ?: "Unknown",

                    ext = file.name
                        ?.substringAfterLast(".", "")
                        ?: "",

                    mtime = file.lastModified()
                )

                //避免多次插入，减少IO
                val oldBook = oldBooks[book.uri]
                if (oldBook == null) {
                    // 新文件
                    dao.insert(book)
                } else if (oldBook.mtime < file.lastModified()) {
                    // 当老版本读取时间小于更新时间时，说明有新版本，文件更新
                    dao.insert(book.copy(id = oldBook.id))
                }
            }
        }
    }

    fun isBook(file: DocumentFile): Boolean {

        val ext = file.name
            ?.substringAfterLast(".", "")
            ?.lowercase()

        return ext in setOf(
            "epub",
            "pdf",
            "mobi",
            "azw3"
        )
    }
}