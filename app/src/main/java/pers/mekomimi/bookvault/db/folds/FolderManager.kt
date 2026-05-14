package pers.mekomimi.bookvault.db.folds

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile

class FolderManager(
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
            FolderEntity(
                uri = uri.toString()
            )
        )
    }

    suspend fun scanAllFolders() {

        val folders = db.folderDao().getAll()

        folders.forEach {

            scanFolder(Uri.parse(it.uri))
        }
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
}