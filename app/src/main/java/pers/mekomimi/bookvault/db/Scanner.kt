package pers.mekomimi.bookvault.db

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import pers.mekomimi.bookvault.db.book.Book
import pers.mekomimi.bookvault.db.book.BookDao
import pers.mekomimi.bookvault.db.folder.Folder

class Scanner(
    private val context: Context,
    private val db: AppDatabase
) {

    suspend fun addFolder(uri: Uri) {

        context.contentResolver
            .takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

        db.folderDao().insert(
            Folder(
                uri = uri.toString(),
                lastScanTime = System.currentTimeMillis()
            )
        )
    }

    private fun scanFolder(uri: Uri) {

        val root = DocumentFile.fromTreeUri(
            context,
            uri
        ) ?: return

        root.listFiles().forEach {

            if (it.isDirectory) {
                scanFolder(it.uri)
            }

            if (it.name?.endsWith(".epub") == true) {

                Log.d(
                    "BOOK",
                    it.name ?: ""
                )
            }
        }
    }

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
        val oldDir: Folder? =db.folderDao().findByUri(uri = dir.uri.toString())

        if (oldDir!=null){
            if (dir.lastModified() <= oldDir.lastScanTime) {
                return
            }
        }

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

                //避免多次插入，减少IO
                val old = dao.findByUri(file.uri.toString())
                if (old == null) {
                    // 新文件
                    dao.insert(book)
                } else if (old.mtime != file.lastModified()) {
                    // 文件更新
                    dao.insert(book.copy(id = old.id))
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