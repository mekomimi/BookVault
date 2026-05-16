package pers.mekomimi.bookvault.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pers.mekomimi.bookvault.MainActivity
import pers.mekomimi.bookvault.db.book.Book
import pers.mekomimi.bookvault.ui.components.Controller
import pers.mekomimi.bookvault.ui.components.ShelfGrid

@Composable
fun ShelfScreen(
    books: List<Book>,
    onRefresh: () -> Unit,
    onAddFolder: () -> Unit,
    context: Context

) {
    //page state
    val pageSize = 9
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = (books.size + pageSize - 1) / pageSize
    val pagedBooks = books
        .drop((currentPage - 1) * pageSize)
        .take(pageSize)

    val activity = context as MainActivity

    LaunchedEffect(Unit) {

        activity.onVolumeDown = {
            if (currentPage < totalPages) {
                currentPage++
            }
        }

        activity.onVolumeUp = {
            if (currentPage > 1) {
                currentPage--
            }
        }
    }

    Column {
        val controllerHeight = 80.dp

        BoxWithConstraints {
            val maxHeight = maxHeight

            ShelfGrid(
                books = pagedBooks,
                context = context,
                modifier = Modifier.height(maxHeight - controllerHeight)
            )
        }

        //控制栏
        Controller(
            currentPage,
            totalPages,
            onPrev = { if (currentPage > 1) currentPage-- },
            onNext = { if (currentPage < totalPages) currentPage++ },
            onRefresh = onRefresh,
            onAddFolder = onAddFolder,
            modifier = Modifier.height(controllerHeight)
        )
    }
}
