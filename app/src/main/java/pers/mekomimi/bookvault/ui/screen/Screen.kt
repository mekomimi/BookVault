package pers.mekomimi.bookvault.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pers.mekomimi.bookvault.db.books.Book
import pers.mekomimi.bookvault.db.books.BookDao
import pers.mekomimi.bookvault.db.books.scanBooks
import pers.mekomimi.bookvault.ui.components.BookItem
import pers.mekomimi.bookvault.ui.components.Controller
import pers.mekomimi.bookvault.ui.components.ListLayout
import java.io.File

@Composable
fun BookScreen(
    pagedBooks: List<Book>,
    currentPage: Int,
    totalPages: Int,
    onPrev: () -> Unit,
    onNext: () -> Unit,
    onRefresh: () -> Unit,
    context: Context
) {
    Scaffold { innerPadding ->
        val controllerHeight = 80.dp

        Column {
            BoxWithConstraints(
                modifier = Modifier.padding(innerPadding)
            ) {
                val itemHeight = (maxHeight - controllerHeight) / 9

                Column {
                    //书籍列表
                    LazyColumn {
                        items(pagedBooks) { book ->
                            BookItem(
                                book,
                                context,
                                modifier = Modifier.height(itemHeight)
                            )
                        }
                    }
                }
            }

            //控制栏
            Controller(
                currentPage,
                totalPages,
                onPrev = onPrev,
                onNext = onNext,
                onRefresh = onRefresh,
                modifier = Modifier.height(controllerHeight)
            )
        }
    }
}


@Composable
fun ListScreen(
    books: List<Book>,
    dao: BookDao,
    context: Context
) {
    val scope = rememberCoroutineScope()

    //page state
    val pageSize = 9
    var currentPage by remember { mutableIntStateOf(1) }
    val totalPages = (books.size + pageSize - 1) / pageSize
    val pagedBooks = books
        .drop((currentPage - 1) * pageSize)
        .take(pageSize)

    Column {
        val controllerHeight = 80.dp

        ListLayout(
            books = pagedBooks,
            context = context,
            controllerHeight = controllerHeight
        )

        //控制栏
        Controller(
            currentPage,
            totalPages,
            { if (currentPage > 1) currentPage-- },
            onNext = { if (currentPage < totalPages) currentPage++ },
            onRefresh = {
                scope.launch(Dispatchers.IO) {
                    scanBooks(File("/storage/emulated/0/Download"), dao)
                }
            },
            modifier = Modifier.height(controllerHeight)
        )
    }
}