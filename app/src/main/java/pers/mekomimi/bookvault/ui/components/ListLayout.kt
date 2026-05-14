package pers.mekomimi.bookvault.ui.components

import android.content.Context
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import pers.mekomimi.bookvault.db.books.Book


@Composable
fun ListLayout(
    books: List<Book>,
    context: Context,
    controllerHeight: Dp
) {
    BoxWithConstraints {
        val itemHeight = (maxHeight - controllerHeight) / 9

        Column {
            //书籍列表
            LazyColumn {
                items(books) { book ->
                    BookItem(
                        book,
                        context,
                        modifier = Modifier.height(itemHeight)
                    )
                }
            }
        }
    }
}