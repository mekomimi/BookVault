package pers.mekomimi.bookvault.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import pers.mekomimi.bookvault.db.Book

@Composable
fun ShelfGrid(
    books: List<Book>,
    context: Context,
    modifier: Modifier
) {
    Column(modifier=modifier) {
        if (!books.isEmpty()){
            ShelfLayout {
                repeat(9) { index ->
                    BookItem(context = context, book = books[index])
                }
            }
        }
    }
}

@Composable
fun ShelfLayout(
    content: @Composable () -> Unit
) {
    Layout(content = content) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val itemWidth = width / 3
        val itemHeight = height / 3

        val placeable = measurables.take(9).map {
            it.measure(
                Constraints.fixed(itemWidth, itemHeight)
            )
        }

        layout(width, height) {
            placeable.forEachIndexed { index, placeable ->
                val row = index / 3
                val col = index % 3

                val x = col * itemWidth
                val y = row * itemHeight

                placeable.place(x, y)
            }
        }
    }
}
