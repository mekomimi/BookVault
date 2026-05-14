package pers.mekomimi.bookvault.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.FileProvider
import pers.mekomimi.bookvault.db.books.Book
import java.io.File

@Composable
fun BookItem(
    book: Book,
    context: Context,
    modifier: Modifier = Modifier

) {
    Column(modifier = modifier) {

        Text(
            text = book.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Button(onClick = {
            val file = File(book.path)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "*/*")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
        }) {
            Text("打开")
        }
    }

}

