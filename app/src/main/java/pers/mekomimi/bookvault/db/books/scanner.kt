package pers.mekomimi.bookvault.db.books

import java.io.File

suspend fun scanBooks(root: File, dao: BookDao) {
    println("exists=${root.exists()} canRead=${root.canRead()}")
    root.listFiles()?.forEach {
        println("listFiles: ${it.absolutePath}")
    } ?: println("listFiles == null")
    println("开始扫描: ${root.absolutePath}")

    root.walkTopDown().forEach { file ->
        //println("扫描到: ${file.absolutePath}")

        if (file.isFile && isBook(file)) {
            println("命中书籍: ${file.absolutePath}")

            val book = Book(
                path = file.absolutePath,
                title = file.nameWithoutExtension,
                ext = file.extension,
                mtime = file.lastModified()
            )
            dao.insert(book)
        }
    }
}

fun isBook(file: File): Boolean {
    return file.extension.lowercase() in setOf("epub", "pdf", "mobi", "azw3")
}