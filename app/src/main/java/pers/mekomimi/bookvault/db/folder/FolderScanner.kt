package pers.mekomimi.bookvault.db.folder

import android.content.Context
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import pers.mekomimi.bookvault.db.AppDatabase
import pers.mekomimi.bookvault.db.books.Book
import pers.mekomimi.bookvault.db.books.BookDao

class FolderScanner(
    private val context: Context,
    private val db: AppDatabase
) {
    suspend fun scanAllFolders() {

        val folders = db.folderDao().getAll()

        for (folder in folders) {

            val root = DocumentFile.fromTreeUri(
                context,
                folder.uri.toUri()
            ) ?: continue

            scanDirectory(root, db.bookDao())
        }
    }

    private suspend fun scanDirectory(
        dir: DocumentFile,
        dao: BookDao
    ) {

        val files = dir.listFiles()

        println("扫描目录: ${dir.name}")
        println("文件数量: ${files.size}")

        for (file in files) {

            println("扫描到: ${file.uri}")

            // 递归目录
            if (file.isDirectory) {

                scanDirectory(file, dao)
            }

            // 处理文件
            else if (file.isFile && isBook(file)) {

                println("命中书籍: ${file.name}")

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

                dao.insert(book)
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